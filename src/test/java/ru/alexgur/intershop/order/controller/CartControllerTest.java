package ru.alexgur.intershop.order.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.model.ActionType;

@SpringBootTest
@AutoConfigureWebTestClient
class CartControllerTest extends BaseTest {

    @Test
    public void getCartItems() throws Exception {
        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void updateCartItemQuantity() throws Exception {
        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient.post().uri("/cart/items/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart.*");

        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void buy() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient.post().uri("/cart/items/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart.*");

        webTestClient.post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/orders");

    }
}