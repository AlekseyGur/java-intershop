package ru.alexgur.intershop;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureWebTestClient
public class ContextTest extends BaseTest {

    @Test
    void testContext() {
        assertNotNull(webTestClient);
    }
}