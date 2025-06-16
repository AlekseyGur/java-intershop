// package ru.alexgur.intershop.item.service;

// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;

// import ru.alexgur.intershop.TestWebConfiguration;
// import ru.alexgur.intershop.item.dto.ItemDto;
// import static org.assertj.core.api.Assertions.assertThat;

// public class ItemServiceImplTest extends TestWebConfiguration {
// @Autowired
// private ItemServiceImpl itemServiceImpl;

// @Test
// void testGet() {
// ItemDto item = itemServiceImpl.get(1L);

// assertThat(item)
// .isNotNull()
// .withFailMessage("Созданной записи должен был быть присвоен ID")
// .extracting(ItemDto::getId)
// .isNotNull();
// }
// }
