package ru.alexgur.intershop.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.mapper.ItemMapper;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.model.Order;

@Mapper(componentModel = "spring", uses = ItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "totalSum", ignore = true)
    OrderDto toDto(Order order);

    Order fromDto(OrderDto order);

    default Mono<Order> fromMonoDto(Mono<OrderDto> order) {
        return order.map(this::fromDto);
    }

    default Flux<Order> fromFluxDto(Flux<OrderDto> orders) {
        return orders.map(this::fromDto);
    }

    default Flux<OrderDto> toFluxDto(Flux<Order> orders) {
        return orders.map(this::toDto);
    }
}