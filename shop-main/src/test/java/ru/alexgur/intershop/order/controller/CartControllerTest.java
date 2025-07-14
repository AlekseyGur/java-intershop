package ru.alexgur.intershop.order.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.service.ItemServiceImpl;
import ru.alexgur.intershop.system.exception.PaymentException;
import ru.alexgur.payment.model.Balance;

@SpringBootTest
@AutoConfigureWebTestClient
class CartControllerTest extends BaseTest {

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

        webTestClient.post().uri("/cart/items/" + firstSavedItemId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient.get().uri("/cart")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void buy() throws Exception {
        when(paymentService.doPayment(anyDouble())).thenReturn(Mono.empty());
        Balance balanceEntity = new Balance(firstSavedItem.getPrice() + 100.0);
        when(paymentService.getBalance()).thenReturn(Mono.just(balanceEntity));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

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

    }

    @Test
    public void buyNotEnoughMoneyError() throws Exception {
        when(paymentService.doPayment(anyDouble()))
                .thenReturn(Mono.error(new PaymentException("Ошибка выполнения платежа")));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", ActionType.PLUS.toString());

        webTestClient.post().uri("/cart/items/" + firstSavedItemId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart");

        webTestClient.post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/error\\?reason=low_balance");

    }
}