package ru.alexgur.intershop.order.mapper;

import java.util.List;

import ru.alexgur.intershop.order.dto.OrderItemDto;
import ru.alexgur.intershop.order.model.OrderItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.alexgur.intershop.item.mapper.ItemMapper;

@Mapper(componentModel = "spring", uses = { ItemMapper.class, OrderMapper.class })
public interface OrderItemMapper {

    OrderItemDto toDto(OrderItem order);

    @Mapping(target = "id", ignore = true)
    OrderItem fromDto(OrderItemDto orderDto);

    List<OrderItem> fromDto(List<OrderItemDto> ordersDto);

    List<OrderItemDto> toDto(List<OrderItem> orders);

}