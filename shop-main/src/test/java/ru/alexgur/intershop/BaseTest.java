package ru.alexgur.intershop;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import com.redis.testcontainers.RedisContainer;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import ru.alexgur.intershop.user.service.CustomReactiveUserDetailsService;
import ru.alexgur.payment.service.PaymentService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

@SpringBootTest(classes = { MainIntershop.class })
@Import(TestSecurityConfig.class)
@EnableWebFluxSecurity
@AutoConfigureWebTestClient
public class BaseTest {

    @Autowired
    public ReactiveWebApplicationContext reactiveWebApplicationContext;

    @Autowired
    public WebTestClient webTestClient;

    @Autowired
    public DatabaseClient databaseClient;

    @MockitoBean
    public PaymentService paymentService;

    @MockitoBean
    public CustomReactiveUserDetailsService customReactiveUserDetailsService;

    @Autowired
    public CacheManager cacheManager;

    private static final RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("intershop-test")
            .withUsername("intershop-test")
            .withPassword("intershop-test");

    private static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:latest")
            .withRealmImportFile("development-realm.json");

    static {
        postgres.start();
        redis.start();
        keycloak.start();
        waitServiceToBeReady(keycloak.getAuthServerUrl());
    }

    private static void waitServiceToBeReady(String healthCheckUrl) {
        boolean ready = false;
        while (!ready) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> response = restTemplate.getForEntity(healthCheckUrl, String.class);
                if (response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                    ready = true;
                }
            } catch (Exception ex) {
                System.err.println("Waiting for Keycloak to start...");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        System.out.println("Keycloak is now fully ready.");
    }

    @AfterEach
    @BeforeEach
    void cleanDb() {
        cacheManager.getCacheNames().forEach(x -> cacheManager.getCache(x).clear());
        databaseClient.sql("DELETE FROM order_items").then().block();
        databaseClient.sql("DELETE FROM orders").then().block();
    }

    @DynamicPropertySource
    static void registerKeycloakProperties(DynamicPropertyRegistry registry) {
        String authServerUrl = keycloak.getAuthServerUrl();

        registry.add("keycloak.auth-server-url", () -> authServerUrl);
        registry.add("keycloak.realm", () -> "master");
        registry.add("keycloak.resource", () -> "test-app");
        registry.add("keycloak.credentials.secret", () -> "your_secret_here");
        registry.add("keycloak.ssl.required", () -> "none");

        System.out.println("=========================");
        System.out.println("Keycloak URL: " + authServerUrl);
        System.out.println("Realm: master");
        System.out.println("Resource: test-app");
        System.out.println("Credentials Secret: your_secret_here");
        System.out.println("SSL Required: none");
        System.out.println("=========================");
    }

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        String host = redis.getHost();
        Integer port = redis.getFirstMappedPort();
        String containerId = redis.getContainerId();

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);

        System.out.println("=========================");
        System.out.println("Redis host: " + host + " port " + port.toString());
        System.out.println("docker exec -it " + containerId + " redis-cli ");
        System.out.println("=========================");
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
        System.out.println("Postgres:");
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