package ru.alexgur.intershop.order.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.intershop.order.dto.OrderItemDto;
import ru.alexgur.intershop.order.model.OrderItem;

@UtilityClass
public class OrderItemMapper {
    public static OrderItemDto toDto(OrderItem order) {
        OrderItemDto orderDto = new OrderItemDto();
        orderDto.setItem(order.getItem());
        orderDto.setOrder(order.getOrder());
        orderDto.setQuantity(order.getQuantity());
        return orderDto;
    }

    public static OrderItem fromDto(OrderItemDto orderDto) {
        OrderItem order = new OrderItem();
        order.setItem(orderDto.getItem());
        order.setOrder(orderDto.getOrder());
        order.setQuantity(orderDto.getQuantity());
        return order;
    }

    public static List<OrderItem> fromDto(List<OrderItemDto> ordersDto) {
        return ordersDto.stream().map(OrderItemMapper::fromDto).toList();
    }

    public static List<OrderItemDto> toDto(List<OrderItem> orders) {
        return orders.stream().map(OrderItemMapper::toDto).toList();
    }
}