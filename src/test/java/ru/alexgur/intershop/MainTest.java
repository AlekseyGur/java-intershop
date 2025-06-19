package ru.yandex.practicum.intershop;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public class MainTest {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("intershop")
            .withUsername("intershop")
            .withPassword("intershop");

    static {
        postgres.start();
    }

    @Autowired
    DatabaseClient databaseClient;

    @AfterEach
    void cleanDb() {
        databaseClient.sql("DELETE FROM orders_items; DELETE FROM orders").then().block();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String host = postgres.getHost();
        Integer port = postgres.getFirstMappedPort();
        String dbName = postgres.getDatabaseName();
        String schema = "r2dbc:postgresql://";

        registry.add("spring.r2dbc.url", () -> schema + host + ":" + port + "/" + dbName);
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }
}
