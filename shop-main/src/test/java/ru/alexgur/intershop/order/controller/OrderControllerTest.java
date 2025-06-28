package ru.alexgur.intershop.order.controller;

import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.service.ItemServiceImpl;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.service.OrderServiceImpl;
import ru.alexgur.payment.model.Balance;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureWebTestClient
class OrderControllerTest extends BaseTest {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    UUID firstSavedItemId;
    ItemDto firstSavedItem;

    @BeforeEach
    public void getFirstSavedItemId() {
        firstSavedItem = itemServiceImpl.getAll(0, 1, "", SortType.ALPHA).block().getContent().get(0);
        firstSavedItemId = firstSavedItem.getId();
    }

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
        when(paymentService.doPayment(anyDouble())).thenReturn(Mono.empty());
        Balance balanceEntity = new Balance(firstSavedItem.getPrice() + 100.0);
        when(paymentService.getBalance()).thenReturn(Mono.just(balanceEntity));

        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", "PLUS");

        webTestClient.post().uri("/cart/items/" + firstSavedItemId)
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

        webTestClient.get().uri("/orders/" + orders.get(0).getId())
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
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/error\\?reason=low_balance");
    }

    @Test
    public void getOrdersMany() throws Exception {

        webTestClient.get().uri("/cart");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient.post().uri("/cart/items/" + firstSavedItemId)
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