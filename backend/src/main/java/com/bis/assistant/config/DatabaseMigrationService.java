package com.bis.assistant.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service("databaseMigrationService")
public class DatabaseMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationService.class);

    public static final String ARCHIVE_USER_EMAIL = "legacy.anonymous@bis.gov.in";
    public static final String ARCHIVE_USER_NAME = "Legacy Archive User";
    public static final String ARCHIVE_USER_PASSWORD_HASH = "$DISABLED$LOCKED_LEGACY_ARCHIVE_NO_LOGIN$";

    private final DataSource dataSource;

    public DatabaseMigrationService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void runMigrationOnStartup() {
        migrate();
    }

    /**
     * Executes the idempotent database migration to safely transition legacy anonymous
     * conversations to user-associated conversations with strict integrity constraints.
     */
    public synchronized void migrate() {
        logger.info("Initializing safe database schema migration for conversation ownership...");

        try (Connection conn = dataSource.getConnection()) {
            // Find existing table names in case-insensitive manner
            String conversationsTable = findActualTableName(conn, "conversations");
            if (conversationsTable == null) {
                logger.info("Table 'conversations' not found yet. Schema will be created by JPA.");
                return;
            }

            String usersTable = findActualTableName(conn, "users");
            if (usersTable == null) {
                logger.info("Table 'users' not found. Creating 'users' table prior to archive user registration...");
                createUsersTableIfNotExists(conn);
                usersTable = findActualTableName(conn, "users");
                if (usersTable == null) {
                    usersTable = "users";
                }
            }

            // Step 1: Add conversations.user_id as nullable column first (if not already present)
            boolean userIdExists = hasColumn(conn, conversationsTable, "user_id");
            if (!userIdExists) {
                logger.info("Step 1: Adding nullable column 'user_id' to '{}' table...", conversationsTable);
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE " + conversationsTable + " ADD COLUMN user_id BIGINT");
                }
                logger.info("Step 1 complete: Added nullable 'user_id' column.");
            } else {
                logger.info("Step 1: Column 'user_id' already present on '{}'.", conversationsTable);
            }

            // Step 2: Create or retrieve dedicated legacy archive user
            Long archiveUserId = getOrCreateArchiveUser(conn, usersTable);
            logger.info("Step 2 complete: Dedicated legacy archive user ID resolved: {} ({})", archiveUserId, ARCHIVE_USER_EMAIL);

            // Step 3: Assign every existing conversation where user_id IS NULL to this archive user
            int assignedCount = 0;
            try (PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE " + conversationsTable + " SET user_id = ? WHERE user_id IS NULL")) {
                updateStmt.setLong(1, archiveUserId);
                assignedCount = updateStmt.executeUpdate();
            }
            if (assignedCount > 0) {
                logger.info("Step 3 complete: Assigned {} legacy anonymous conversation(s) to archive user ID {}.", assignedCount, archiveUserId);
            } else {
                logger.info("Step 3: No unassigned conversations with NULL user_id found.");
            }

            // Step 4: Remove old unique constraint/index enforcing globally unique session_identifier
            removeLegacySessionIdentifierConstraints(conn, conversationsTable);

            // Step 5: Add Foreign Key constraint from conversations.user_id to users.id
            addForeignKeyConstraintIfNotExists(conn, conversationsTable, usersTable);

            // Step 6: Add composite unique constraint UNIQUE(user_id, session_identifier)
            addCompositeUniqueConstraintIfNotExists(conn, conversationsTable);

            // Step 7: Make conversations.user_id NOT NULL
            makeColumnNotNull(conn, conversationsTable, "user_id");

            logger.info("Database schema migration for conversation ownership successfully completed.");

        } catch (Exception e) {
            logger.error("Database migration error: {}", e.getMessage(), e);
            throw new RuntimeException("Database migration failed: " + e.getMessage(), e);
        }
    }

    private String findActualTableName(Connection conn, String expectedName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, expectedName, new String[]{"TABLE"})) {
            if (rs.next()) return rs.getString("TABLE_NAME");
        }
        try (ResultSet rs = meta.getTables(null, null, expectedName.toUpperCase(), new String[]{"TABLE"})) {
            if (rs.next()) return rs.getString("TABLE_NAME");
        }
        try (ResultSet rs = meta.getTables(null, null, expectedName.toLowerCase(), new String[]{"TABLE"})) {
            if (rs.next()) return rs.getString("TABLE_NAME");
        }
        return null;
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            if (rs.next()) return true;
        }
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName.toUpperCase())) {
            if (rs.next()) return true;
        }
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName.toLowerCase())) {
            if (rs.next()) return true;
        }
        return false;
    }

    private void createUsersTableIfNotExists(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                    "full_name VARCHAR(150), " +
                    "email VARCHAR(150) NOT NULL UNIQUE, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "created_at TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP NOT NULL)");
        } catch (SQLException e) {
            logger.debug("createUsersTableIfNotExists notice: {}", e.getMessage());
        }
    }

    private Long getOrCreateArchiveUser(Connection conn, String usersTable) throws SQLException {
        // Query existing archive user
        try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT id FROM " + usersTable + " WHERE LOWER(email) = ?")) {
            checkStmt.setString(1, ARCHIVE_USER_EMAIL.toLowerCase());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        // Insert archive user with non-loginable invalid password hash
        try (PreparedStatement insertStmt = conn.prepareStatement(
                "INSERT INTO " + usersTable + " (full_name, email, password_hash, created_at, updated_at) " +
                        "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
                Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, ARCHIVE_USER_NAME);
            insertStmt.setString(2, ARCHIVE_USER_EMAIL);
            insertStmt.setString(3, ARCHIVE_USER_PASSWORD_HASH);
            insertStmt.executeUpdate();

            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }
        }

        // Fallback query if generated key was not returned directly
        try (PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT id FROM " + usersTable + " WHERE LOWER(email) = ?")) {
            checkStmt.setString(1, ARCHIVE_USER_EMAIL.toLowerCase());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        throw new IllegalStateException("Failed to create or retrieve legacy archive user ID.");
    }

    private void removeLegacySessionIdentifierConstraints(Connection conn, String conversationsTable) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getIndexInfo(null, null, conversationsTable, true, false)) {
                List<String> indexesToDrop = new ArrayList<>();
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    String columnName = rs.getString("COLUMN_NAME");
                    if (indexName != null && columnName != null) {
                        if (columnName.equalsIgnoreCase("session_identifier") &&
                                !indexName.toLowerCase().contains("user_session") &&
                                !indexName.toLowerCase().contains("pkey")) {
                            indexesToDrop.add(indexName);
                        }
                    }
                }
                for (String idx : indexesToDrop) {
                    try (Statement stmt = conn.createStatement()) {
                        try {
                            stmt.execute("ALTER TABLE " + conversationsTable + " DROP CONSTRAINT " + idx);
                            logger.info("Step 4: Dropped legacy unique constraint '{}' on {}.", idx, conversationsTable);
                        } catch (SQLException e1) {
                            stmt.execute("DROP INDEX IF EXISTS " + idx);
                            logger.info("Step 4: Dropped legacy unique index '{}' on {}.", idx, conversationsTable);
                        }
                    } catch (Exception e) {
                        logger.debug("Drop notice for index/constraint {}: {}", idx, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Index metadata inspection notice: {}", e.getMessage());
        }

        String[] candidateNames = {
                "conversations_session_identifier_key",
                "uk_conversations_session_identifier",
                "uk_session_identifier",
                "UK_CONVERSATIONS_SESSION_IDENTIFIER"
        };
        for (String name : candidateNames) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE " + conversationsTable + " DROP CONSTRAINT IF EXISTS " + name);
            } catch (Exception ignored) {
            }
        }
    }

    private void addForeignKeyConstraintIfNotExists(Connection conn, String conversationsTable, String usersTable) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getImportedKeys(null, null, conversationsTable)) {
                while (rs.next()) {
                    String fkCol = rs.getString("FKCOLUMN_NAME");
                    String pkTable = rs.getString("PKTABLE_NAME");
                    if (fkCol != null && fkCol.equalsIgnoreCase("user_id") &&
                            pkTable != null && pkTable.equalsIgnoreCase(usersTable)) {
                        logger.info("Step 5: Foreign key on {}(user_id) -> {}(id) already exists.", conversationsTable, usersTable);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("FK metadata inspection notice: {}", e.getMessage());
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + conversationsTable +
                    " ADD CONSTRAINT fk_conversations_user FOREIGN KEY (user_id) REFERENCES " + usersTable + "(id)");
            logger.info("Step 5 complete: Added foreign key constraint 'fk_conversations_user' on {}(user_id) -> {}(id).", conversationsTable, usersTable);
        } catch (SQLException e) {
            logger.debug("Foreign key constraint notice (may already exist): {}", e.getMessage());
        }
    }

    private void addCompositeUniqueConstraintIfNotExists(Connection conn, String conversationsTable) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getIndexInfo(null, null, conversationsTable, true, false)) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    if (indexName != null && indexName.equalsIgnoreCase("uk_conversations_user_session")) {
                        logger.info("Step 6: Composite unique constraint 'uk_conversations_user_session' already exists on {}.", conversationsTable);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Composite constraint metadata inspection notice: {}", e.getMessage());
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + conversationsTable +
                    " ADD CONSTRAINT uk_conversations_user_session UNIQUE (user_id, session_identifier)");
            logger.info("Step 6 complete: Added composite unique constraint 'uk_conversations_user_session' on {}(user_id, session_identifier).", conversationsTable);
        } catch (SQLException e) {
            logger.debug("Composite unique constraint notice (may already exist): {}", e.getMessage());
        }
    }

    private void makeColumnNotNull(Connection conn, String conversationsTable, String columnName) {
        try {
            DatabaseMetaData meta = conn.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, conversationsTable, columnName)) {
                if (rs.next()) {
                    String isNullable = rs.getString("IS_NULLABLE");
                    if ("NO".equalsIgnoreCase(isNullable)) {
                        logger.info("Step 7: Column '{}.{}' is already NOT NULL.", conversationsTable, columnName);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("Column nullability inspection notice: {}", e.getMessage());
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + conversationsTable + " ALTER COLUMN " + columnName + " SET NOT NULL");
            logger.info("Step 7 complete: Set '{}.{}' to NOT NULL.", conversationsTable, columnName);
        } catch (SQLException e) {
            logger.debug("SET NOT NULL notice (may already be NOT NULL): {}", e.getMessage());
        }
    }
}
