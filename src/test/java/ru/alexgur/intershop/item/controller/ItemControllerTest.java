package ru.alexgur.intershop.item.controller;

import ru.alexgur.intershop.MainTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;

@AutoConfigureWebTestClient
class ItemControllerTest extends MainTest {

    @Test
    public void getHomePageLoaded() throws Exception {
        webTestClient.get().uri("/")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void getItemPage() throws Exception {
        webTestClient.get().uri("/items/1")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }
}