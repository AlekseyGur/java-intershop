package ru.alexgur.intershop.order.model;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.relational.core.mapping.Column;

import lombok.Data;

@Data
public class OrderItemKey implements Serializable {
    @Column("order_id")
    private UUID orderId;

    @Column("item_id")
    private UUID itemId;
}