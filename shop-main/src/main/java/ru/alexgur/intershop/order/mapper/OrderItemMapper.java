package ru.alexgur.intershop.order.mapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import ru.alexgur.intershop.order.dto.OrderItemDto;
import ru.alexgur.intershop.order.model.OrderItem;

import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderItemMapper {

    public OrderItemDto toDto(OrderItem order) {
        if (order == null) {
            return null;
        }
        OrderItemDto dto = new OrderItemDto();
        dto.setQuantity(order.getQuantity());
        return dto;
    }

    public OrderItem fromDto(OrderItemDto orderDto) {
        if (orderDto == null) {
            return null;
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(orderDto.getQuantity());
        return orderItem;
    }

    public List<OrderItem> fromDto(List<OrderItemDto> orderDtos) {
        return orderDtos.stream()
                .filter(Objects::nonNull)
                .map(OrderItemMapper::fromDto)
                .collect(Collectors.toList());
    }
}