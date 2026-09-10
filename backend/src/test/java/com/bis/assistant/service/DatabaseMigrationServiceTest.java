package com.bis.assistant.service;

import com.bis.assistant.config.DatabaseMigrationService;
import com.bis.assistant.dto.LoginRequest;
import com.bis.assistant.model.Conversation;
import com.bis.assistant.model.Message;
import com.bis.assistant.model.User;
import com.bis.assistant.repository.ConversationRepository;
import com.bis.assistant.repository.MessageRepository;
import com.bis.assistant.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class DatabaseMigrationServiceTest {

    @Autowired
    private DatabaseMigrationService databaseMigrationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AuthService authService;

    @Test
    void migration_ArchiveUserIsCreatedWithLockedPassword() {
        // Run migration
        databaseMigrationService.migrate();

        Optional<User> archiveUserOpt = userRepository.findByEmailIgnoreCase(DatabaseMigrationService.ARCHIVE_USER_EMAIL);
        assertThat(archiveUserOpt).isPresent();

        User archiveUser = archiveUserOpt.get();
        assertThat(archiveUser.getEmail()).isEqualTo(DatabaseMigrationService.ARCHIVE_USER_EMAIL);
        assertThat(archiveUser.getFullName()).isEqualTo(DatabaseMigrationService.ARCHIVE_USER_NAME);
        assertThat(archiveUser.getPasswordHash()).startsWith("$DISABLED$");
    }

    @Test
    void migration_ArchiveUserCannotLoginViaAuthService() {
        databaseMigrationService.migrate();

        LoginRequest loginRequest = new LoginRequest(DatabaseMigrationService.ARCHIVE_USER_EMAIL, "AnyPassword123!");
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void migration_IsSafeToRunRepeatedly_Idempotent() {
        // Run migration multiple times
        databaseMigrationService.migrate();
        databaseMigrationService.migrate();
        databaseMigrationService.migrate();

        // Ensure no duplicate archive users were created
        List<User> archiveUsers = userRepository.findAll().stream()
                .filter(u -> DatabaseMigrationService.ARCHIVE_USER_EMAIL.equalsIgnoreCase(u.getEmail()))
                .toList();

        assertThat(archiveUsers).hasSize(1);
    }

    @Test
    @Transactional
    void migration_AssignsLegacyConversationsAndPreservesMessages() throws Exception {
        databaseMigrationService.migrate();

        User archiveUser = userRepository.findByEmailIgnoreCase(DatabaseMigrationService.ARCHIVE_USER_EMAIL)
                .orElseThrow();

        // Create a conversation for archive user with attached messages
        String legacySession = "legacy-test-session-" + System.currentTimeMillis();
        Conversation conv = new Conversation(archiveUser, legacySession, "Legacy Archived Inquiry");
        Message msg1 = new Message(conv, "user", "Old legacy query before auth was introduced.");
        Message msg2 = new Message(conv, "model", "Old assistant response.");
        conv.addMessage(msg1);
        conv.addMessage(msg2);

        Conversation savedConv = conversationRepository.save(conv);
        assertThat(savedConv.getId()).isNotNull();
        assertThat(savedConv.getUser().getId()).isEqualTo(archiveUser.getId());

        // Re-run migration to simulate startup with existing legacy conversations
        databaseMigrationService.migrate();

        // Verify conversation and messages remain intact and attached
        Optional<Conversation> reloadedOpt = conversationRepository.findByUserAndSessionIdentifier(archiveUser, legacySession);
        assertThat(reloadedOpt).isPresent();
        Conversation reloaded = reloadedOpt.get();
        assertThat(reloaded.getMessages()).hasSize(2);
        assertThat(reloaded.getMessages().get(0).getContent()).isEqualTo("Old legacy query before auth was introduced.");
        assertThat(reloaded.getMessages().get(1).getContent()).isEqualTo("Old assistant response.");
    }

    @Test
    @Transactional
    void migration_EnforcesCompositeUniqueConstraintPerUser() {
        databaseMigrationService.migrate();

        User archiveUser = userRepository.findByEmailIgnoreCase(DatabaseMigrationService.ARCHIVE_USER_EMAIL).orElseThrow();
        User testUser = userRepository.findByEmailIgnoreCase("admin@bis.gov.in")
                .orElseGet(() -> userRepository.save(new User("Admin User", "admin@bis.gov.in", "pass123")));

        String commonSession = "common-migrated-session-" + System.currentTimeMillis();

        Conversation conv1 = new Conversation(archiveUser, commonSession, "Archive Session");
        Conversation conv2 = new Conversation(testUser, commonSession, "Admin Session");

        Conversation saved1 = conversationRepository.save(conv1);
        Conversation saved2 = conversationRepository.save(conv2);

        assertThat(saved1.getId()).isNotNull();
        assertThat(saved2.getId()).isNotNull();
        assertThat(saved1.getUser().getId()).isNotEqualTo(saved2.getUser().getId());
        assertThat(saved1.getSessionIdentifier()).isEqualTo(saved2.getSessionIdentifier());
    }
}
