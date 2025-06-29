package ru.alexgur.intershop.item.controller;

import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.service.ItemServiceImpl;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureWebTestClient
class ItemControllerTest extends BaseTest {

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    UUID firstSavedItemId;

    @BeforeEach
    public void getFirstSavedItemId() {
        ItemDto savedItem = itemServiceImpl.getAll(0, 1, null, null).block().getContent().get(0);
        firstSavedItemId = savedItem.getId();
    }

    @Test
    public void getHomePageLoaded() throws Exception {
        webTestClient.get().uri("/")
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }

    @Test
    public void getItemPage() throws Exception {
        webTestClient.get().uri("/items/" + firstSavedItemId)
                .exchange()
                .expectBody()
                .consumeWith(this::hasStatusOkAndClosedHtml);
    }
}