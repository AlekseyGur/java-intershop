package ru.alexgur.intershop.order.dto;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Flux;
import ru.alexgur.intershop.item.dto.ItemDto;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Boolean isPaid = false;
    private Flux<ItemDto> items = Flux.empty();
    private String deliveryAddress;
    private String contactPhone;
    private String contactEmail;
    private Double totalSum = 0.0;
}
