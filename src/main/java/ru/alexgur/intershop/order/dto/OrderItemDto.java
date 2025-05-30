package ru.alexgur.intershop.order.dto;

import lombok.Getter;
import lombok.Setter;
import ru.alexgur.intershop.item.model.Item;
import ru.alexgur.intershop.order.model.Order;
import jakarta.persistence.*;

@Getter
@Setter
@Table(name = "orders")
public class OrderItemDto {
    private Order order;
    private Item item;
    private Integer quantity;
}
