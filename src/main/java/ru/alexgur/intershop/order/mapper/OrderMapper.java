package ru.alexgur.intershop.order.mapper;

import java.util.List;

import lombok.experimental.UtilityClass;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.model.Order;

@UtilityClass
public class OrderMapper {
    public static OrderDto toDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setItems(order.getItems());
        orderDto.setIsPaid(order.getIsPaid());
        orderDto.setContactEmail(order.getContactEmail());
        orderDto.setContactPhone(order.getContactPhone());
        orderDto.setDeliveryAddress(order.getDeliveryAddress());
        return orderDto;
    }

    public static Order toOrder(OrderDto orderDto) {
        Order order = new Order();
        order.setId(orderDto.getId());
        order.setId(orderDto.getId());
        order.setItems(orderDto.getItems());
        order.setIsPaid(orderDto.getIsPaid());
        order.setContactEmail(orderDto.getContactEmail());
        order.setContactPhone(orderDto.getContactPhone());
        order.setDeliveryAddress(orderDto.getDeliveryAddress());
        return order;
    }

    public static List<Order> toOrder(List<OrderDto> ordersDto) {
        return ordersDto.stream().map(OrderMapper::toOrder).toList();
    }

    public static List<OrderDto> toDto(List<Order> orders) {
        return orders.stream().map(OrderMapper::toDto).toList();
    }
}