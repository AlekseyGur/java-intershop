package ru.alexgur.intershop.order.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@ToString
@Table(name = "orders")
public class Order {
    @Id
    private Long id;

    @Column("is_paid")
    private Boolean isPaid = false;

    @Column("delivery_address")
    private String deliveryAddress;

    @Column("contact_phone")
    private String contactPhone;

    @Column("contact_email")
    private String contactEmail;
}
