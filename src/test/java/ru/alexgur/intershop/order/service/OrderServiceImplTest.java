package ru.alexgur.intershop.order.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.order.dto.OrderDto;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
class OrderServiceImplTest extends BaseTest {

    @Autowired
    private OrderServiceImpl orderServiceImpl;

    void changeItemCountInCartUsingEndpont(String action) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("action", action);

        webTestClient.post().uri("/cart/items/1")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(params)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/cart.*");
    }

    void createOrderUsingEndpont() {
        webTestClient.post().uri("/cart/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", "/orders");
    }

    @Test
    void testCreateBuyCartToMakeOrder() {
        changeItemCountInCartUsingEndpont("PLUS");
        createOrderUsingEndpont();

        List<OrderDto> order = orderServiceImpl.getAll().collectList().block();

        assertThat(order).isNotNull();

        assertThat(order.size()).isEqualTo(1);

        assertThat(order.get(0).getItems().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testCartAddOneItem() {
        changeItemCountInCartUsingEndpont("PLUS");

        OrderDto order = orderServiceImpl.getCartOrCreateNew().block();

        assertThat(order).isNotNull();

        assertThat(order.getItems().size()).isEqualTo(1);

        assertThat(order.getItems().get(0).getId()).isEqualTo(1L);

        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void testCartAddTwoSameItems() {
        changeItemCountInCartUsingEndpont("PLUS");
        changeItemCountInCartUsingEndpont("PLUS");

        OrderDto order = orderServiceImpl.getCartOrCreateNew().block();

        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void testCartAddTwoSameItemsAndRemoveOne() {
        changeItemCountInCartUsingEndpont("Plus");
        changeItemCountInCartUsingEndpont("pluS");
        changeItemCountInCartUsingEndpont("MiNuS");

        OrderDto order = orderServiceImpl.getCartOrCreateNew().block();

        assertThat(order.getItems().get(0).getQuantity()).isEqualTo(1);
    }

    @Test
    void testCartAddAndRemoveSameItem() {
        changeItemCountInCartUsingEndpont("Plus");
        changeItemCountInCartUsingEndpont("pluS");
        changeItemCountInCartUsingEndpont("MiNuS");
        changeItemCountInCartUsingEndpont("MiNuS");

        OrderDto order = orderServiceImpl.getCartOrCreateNew().block();

        assertThat(order.getItems().size()).isEqualTo(0);
    }

}
