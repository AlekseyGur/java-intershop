package ru.alexgur.intershop.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

@Configuration
public class DatabaseConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public PostgreSQLContainer<?> postgresqlContainer() {
        return new PostgreSQLContainer<>("postgres:latest");
    }
}