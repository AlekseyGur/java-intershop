package ru.alexgur.intershop.order.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.model.Order;

@UtilityClass
public class OrderMapper {
    public static OrderDto toDto(Order order) {
        OrderDto OrderDto = new OrderDto();
        OrderDto.setId(order.getId());
        // OrderDto.setDescription(order.getDescription());
        // OrderDto.setOrderorId(order.getOrderorId());
        // OrderDto.setCreated(order.getCreated());
        return OrderDto;
    }

    public static Order toOrder(OrderDto OrderDto) {
        Order order = new Order();
        order.setId(OrderDto.getId());
        // order.setDescription(OrderDto.getDescription());
        // order.setOrderorId(OrderDto.getOrderorId());
        // order.setCreated(OrderDto.getCreated());
        return order;
    }

    public static List<Order> toOrder(List<OrderDto> itemsDto) {
        return itemsDto.stream().map(OrderMapper::toOrder).toList();
    }

    public static List<OrderDto> toDto(List<Order> items) {
        return items.stream().map(OrderMapper::toDto).toList();
    }
}