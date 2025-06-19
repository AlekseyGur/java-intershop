package ru.alexgur.intershop.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;

import ru.alexgur.intershop.MainTest;
import ru.alexgur.intershop.item.dto.ItemDto;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient
public class ItemServiceImplTest extends MainTest {
    @Autowired
    private ItemServiceImpl itemServiceImpl;

    @Test
    void testGet() {
        ItemDto item = itemServiceImpl.get(1L).block();

        assertThat(item)
                .isNotNull()
                .withFailMessage("Созданной записи должен был быть присвоен ID")
                .extracting(ItemDto::getId)
                .isNotNull();
    }
}
