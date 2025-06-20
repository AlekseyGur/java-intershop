package ru.alexgur.intershop.order.controller;

import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.model.ActionType;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@SpringBootTest
@AutoConfigureWebTestClient
class OrderControllerTest extends BaseTest {

    @Test
    public void getOrders() throws Exception {
        webTestClient.get().uri("/orders")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void getOrderById() throws Exception {
        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        webTestClient.get().uri("/orders/1")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void getOrdersMany() throws Exception {

        webTestClient.get().uri("/cart");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient.post().uri("/cart/items/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart*");

        webTestClient.get().uri("/orders")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }
}