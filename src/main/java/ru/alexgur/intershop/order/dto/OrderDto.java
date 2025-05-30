package ru.alexgur.intershop.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private boolean isPaid = false;
}
