package ru.alexgur.intershop.order.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ru.alexgur.intershop.item.dto.ItemDto;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Boolean isPaid = false;
    private List<ItemDto> items = List.of();
    private String deliveryAddress;
    private String contactPhone;
    private String contactEmail;
    private Double totalSum = 0.0;
}
