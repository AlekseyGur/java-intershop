package ru.alexgur.intershop.order.dto;

import lombok.Getter;
import lombok.Setter;
import ru.alexgur.intershop.item.model.Item;
import ru.alexgur.intershop.order.model.Order;

@Getter
@Setter
public class OrderItemDto {
    private Order order;
    private Item item;
    private Integer quantity;
}
