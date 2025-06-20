package ru.alexgur.intershop.order.controller;

import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.service.OrderServiceImpl;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class OrderControllerTest extends BaseTest {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Test
    public void getOrders() throws Exception {
        webTestClient.get().uri("/orders")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void getWrongOrder() throws Exception {
        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        webTestClient.get().uri("/orders/999999")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void getOrderById() throws Exception {
        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", "PLUS");

        webTestClient.post().uri("/cart/items/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient.post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/orders");

        List<OrderDto> orders = orderServiceImpl.getAll().collectList().block();

        assertThat(orders).isNotNull();

        assertThat(orders.size()).isEqualTo(1);

        Long orderId = orders.get(0).getId();

        webTestClient.get().uri("/orders/" + orderId)
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void cantBuyIfNoItems() throws Exception {
        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        webTestClient.post().uri("/cart/buy")
                .exchange()
                .expectStatus().is4xxClientError();
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
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient.get().uri("/orders")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }
}