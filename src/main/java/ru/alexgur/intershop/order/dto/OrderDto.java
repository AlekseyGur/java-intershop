package ru.alexgur.intershop.order.dto;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.alexgur.intershop.item.dto.ItemDto;

@Getter
@Setter
@ToString
public class OrderDto {
    private UUID id;
    private Boolean isPaid = false;
    private List<ItemDto> items = List.of();
    private String deliveryAddress;
    private String contactPhone;
    private String contactEmail;
    private Double totalSum = 0.0;
}
