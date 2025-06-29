package ru.alexgur.intershop;

import static org.junit.Assert.*;

import org.junit.jupiter.api.Test;

public class ContextTest extends BaseTest {

    @Test
    void testContext() {
        assertNotNull(webTestClient);
    }
}