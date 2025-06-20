package ru.alexgur.intershop.order.model;

import java.io.Serializable;

import org.springframework.data.relational.core.mapping.Column;

import lombok.Data;

@Data
// @Embeddable
public class OrderItemKey implements Serializable {
    @Column("order_id")
    private Long orderId;

    @Column("item_id")
    private Long itemId;
}