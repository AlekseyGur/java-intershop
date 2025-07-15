package ru.alexgur.intershop.order.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import reactor.core.publisher.Mono;
import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.service.ItemServiceImpl;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.user.model.CustomUserDetails;
import ru.alexgur.payment.model.Balance;

@SpringBootTest
@AutoConfigureWebTestClient
class OrderServiceImplTest extends BaseTest {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    UUID firstSavedItemId;
    ItemDto firstSavedItem;
    CustomUserDetails userSimple;
    CustomUserDetails userAdmin;

    @BeforeEach
    public void getFirstSavedItemId() {
        firstSavedItem = itemServiceImpl.getAll(0, 1, "", SortType.ALPHA).block().getContent().get(0);
        firstSavedItemId = firstSavedItem.getId();

        // userAdmin = customReactiveUserDetailsService.findByUsername("admin").block();
        // userSimple = customReactiveUserDetailsService.findByUsername("user").block();
    }

    void changeItemCountInCartUsingEndpont(String action) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", action);

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimple))
                .post().uri("/cart/items/" + firstSavedItemId)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart*");
    }

    void createOrderUsingEndpont() {
        when(paymentService.doPayment(any(), anyDouble())).thenReturn(Mono.empty());
        Balance balanceEntity = new Balance(firstSavedItem.getPrice() + 100.0);
        when(paymentService.getBalance(any())).thenReturn(Mono.just(balanceEntity));

        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser(userSimple))
                .post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/orders");
    }

    @Test
    void testCreateBuyCartToMakeOrder() {
        changeItemCountInCartUsingEndpont("PLUS");
        createOrderUsingEndpont();

        List<OrderDto> order = orderServiceImpl.getAll(userSimple.getUserId()).collectList().block();

        assertThat(order).isNotNull();

        assertThat(order.size()).isEqualTo(1);

        assertThat(order.get(0).getItems().get(0).getId()).isEqualTo(firstSavedItemId);
    }

    @Test
    void testCartAddOneItem() {
        changeItemCountInCartUsingEndpont("PLUS");

        OrderDto order = orderServiceImpl.getCart(userSimple.getUserId()).block();

        assertThat(order).isNotNull();

        assertThat(order.getItems().size()).isEqualTo(1);

        assertThat(order.getItems().get(0).getId()).isEqualTo(firstSavedItemId);

        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void testCartAddTwoSameItems() {
        changeItemCountInCartUsingEndpont("PLUS");
        changeItemCountInCartUsingEndpont("PLUS");

        OrderDto order = orderServiceImpl.getCart(userSimple.getUserId()).block();

        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void testCartAddTwoSameItemsAndRemoveOne() {
        changeItemCountInCartUsingEndpont("Plus");
        changeItemCountInCartUsingEndpont("pluS");
        changeItemCountInCartUsingEndpont("MiNuS");

        OrderDto order = orderServiceImpl.getCart(userSimple.getUserId()).block();

        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void testCartAddAndRemoveSameItem() {
        changeItemCountInCartUsingEndpont("Plus");
        changeItemCountInCartUsingEndpont("pluS");
        changeItemCountInCartUsingEndpont("MiNuS");
        changeItemCountInCartUsingEndpont("MiNuS");

        OrderDto order = orderServiceImpl.getCart(userSimple.getUserId()).block();

        assertThat(order.getItems().size()).isEqualTo(0);
    }

}
