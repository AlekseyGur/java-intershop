package ru.alexgur.intershop.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.alexgur.intershop.item.model.Item;
import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
