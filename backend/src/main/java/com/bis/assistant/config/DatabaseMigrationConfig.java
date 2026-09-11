package com.bis.assistant.config;

import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseMigrationConfig {

    /**
     * Guarantees that DatabaseMigrationService executes its migration on DataSource
     * before LocalContainerEntityManagerFactoryBean starts and runs Hibernate DDL updates.
     */
    @Bean
    public static EntityManagerFactoryDependsOnPostProcessor entityManagerFactoryDependsOnDatabaseMigration() {
        return new EntityManagerFactoryDependsOnPostProcessor("databaseMigrationService");
    }
}
