package ru.alexgur.intershop.system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@Profile("dev")
@Configuration
public class SetupTestContainer {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("intershop")
            .withUsername("intershop")
            .withPassword("intershop");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String host = postgres.getHost();
        Integer port = postgres.getFirstMappedPort();
        String dbName = postgres.getDatabaseName();
        String username = postgres.getUsername();
        String containerId = postgres.getContainerId();
        String schema = "r2dbc:postgresql://";

        registry.add("spring.r2dbc.url", () -> schema + host + ":" + port + "/" + dbName);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        System.out.println("=========================");
        System.out.println("docker exec -it " + containerId + " psql -U " + username + " -d " + dbName);
        System.out.println("=========================");
    }
}