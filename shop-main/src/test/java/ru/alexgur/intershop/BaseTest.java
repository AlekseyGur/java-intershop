package ru.alexgur.intershop;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = { Main.class })
public class BaseTest {

    @Autowired
    public ReactiveWebApplicationContext reactiveWebApplicationContext;

    @Autowired
    public WebTestClient webTestClient;

    @Autowired
    public DatabaseClient databaseClient;

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("intershop-test")
            .withUsername("intershop-test")
            .withPassword("intershop-test");

    static {
        postgres.start();
    }

    @AfterEach
    @BeforeEach
    void cleanDb() {
        databaseClient.sql("DELETE FROM order_items").then().block();
        databaseClient.sql("DELETE FROM orders").then().block();
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

    public static String genEmail() {
        return genString(15) + "@test.test";
    }

    public static String genString(int length) {
        String randomChars = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomChars += (char) (random.nextInt(26) + 'a');
        }
        return randomChars;
    }

    public <T> void hasStatusOkAndClosedHtml(EntityExchangeResult<T> result) {
        HttpStatusCode status = result.getStatus();
        assertThat(status.value(), is(HttpStatus.OK.value()));

        MediaType contentType = result.getResponseHeaders().getContentType();

        assertNotNull(contentType);

        assertTrue(contentType.toString().contains(MediaType.TEXT_HTML_VALUE.toString()));

        String responseBody = new String(result.getResponseBodyContent(), StandardCharsets.UTF_8);
        assertTrue(responseBody.contains("</html>"), "Ответ должен содержать закрывающий тег </html>");
    }
}