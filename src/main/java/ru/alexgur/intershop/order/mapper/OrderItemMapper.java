package ru.alexgur.intershop.order.mapper;

import java.util.List;

import ru.alexgur.intershop.order.dto.OrderItemDto;
import ru.alexgur.intershop.order.model.OrderItem;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.alexgur.intershop.item.mapper.ItemMapper;

@Mapper(componentModel = "spring", uses = { ItemMapper.class, OrderMapper.class })
public interface OrderItemMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItemDto toDto(OrderItem order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "itemId", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    OrderItem fromDto(OrderItemDto orderDto);

    List<OrderItem> fromDto(List<OrderItemDto> ordersDto);

}