package ru.alexgur.intershop.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table("order_items")
public class OrderItem {
    @Id
    private UUID id;

    @Column("order_id")
    private UUID orderId;

    @Column("item_id")
    private UUID itemId;

    @Column("quantity")
    private Integer quantity;
}
