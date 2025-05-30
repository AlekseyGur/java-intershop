package ru.alexgur.intershop.order.model;

import lombok.Getter;
import lombok.Setter;
import ru.alexgur.intershop.item.model.Item;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
