package ru.alexgur.intershop.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.alexgur.intershop.item.model.Item;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@ToString
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
    @JoinTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> items = List.of();

    @Size(max = 255)
    private String deliveryAddress;

    @Size(max = 100)
    private String contactPhone;

    @Size(max = 100)
    private String contactEmail;
}
