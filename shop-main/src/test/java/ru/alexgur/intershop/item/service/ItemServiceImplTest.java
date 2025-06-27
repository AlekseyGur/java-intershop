package ru.alexgur.intershop.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

import ru.alexgur.intershop.BaseTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
public class ItemServiceImplTest extends BaseTest {

    @Autowired
    private ItemServiceImpl itemServiceImpl;

    @Test
    void testGet() {
        ItemDto savedItem = itemServiceImpl.getAll(0, 1, null, null).block().getContent().blockFirst();

        ItemDto item = itemServiceImpl.get(savedItem.getId()).block();

        assertThat(item).isNotNull();

        assertThat(item)
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(ItemDto::getId)
                .isNotNull();
    }

    @Test
    void testCheckItemExistById() {
        ItemDto savedItem = itemServiceImpl.getAll(0, 1, null, null).block().getContent().blockFirst();

        Boolean res = itemServiceImpl.checkIdExist(savedItem.getId()).block();

        assertThat(res).isNotNull();
    }

}
