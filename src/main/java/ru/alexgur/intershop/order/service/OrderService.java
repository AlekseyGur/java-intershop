package ru.alexgur.intershop.order.service;

import java.util.List;

import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.order.dto.OrderDto;

public interface OrderService {
    List<OrderDto> getAll();

    OrderDto get(Long id);

    void updateCartQuantity(Long itemId, ActionType action);

    OrderDto getCart();

    OrderDto buyItems();

    boolean checkIdExist(Long id);
}