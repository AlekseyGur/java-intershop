package ru.alexgur.intershop.order.mapper;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.model.Order;

@UtilityClass
public class OrderMapper {
    public static OrderDto toDto(Order order) {
        if (order == null) {
            return null;
        }
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setContactPhone(order.getContactPhone());
        dto.setContactEmail(order.getContactEmail());
        dto.setIsPaid(order.getIsPaid());
        return dto;
    }

    public static Order fromDto(OrderDto dto) {
        if (dto == null) {
            return null;
        }
        Order order = new Order();
        order.setId(dto.getId());
        order.setUserId(dto.getUserId());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setContactPhone(dto.getContactPhone());
        order.setContactEmail(dto.getContactEmail());
        order.setIsPaid(dto.getIsPaid());
        return order;
    }

    public static Mono<Order> fromMonoDto(Mono<OrderDto> order) {
        return order.map(OrderMapper::fromDto);
    }

    public static Flux<Order> fromFluxDto(Flux<OrderDto> orders) {
        return orders.map(OrderMapper::fromDto);
    }

    public static Flux<OrderDto> toFluxDto(Flux<Order> orders) {
        return orders.map(OrderMapper::toDto);
    }
}