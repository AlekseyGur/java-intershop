package ru.alexgur.intershop.item.controller;

import ru.alexgur.intershop.BaseTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureWebTestClient
class ItemControllerTest extends BaseTest {

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