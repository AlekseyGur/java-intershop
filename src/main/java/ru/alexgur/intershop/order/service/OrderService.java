package ru.alexgur.intershop.order.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.order.dto.OrderDto;

public interface OrderService {
    Flux<OrderDto> getAll();

    Mono<OrderDto> get(Long id);

    Mono<Void> updateCartQuantity(Long itemId, ActionType action);

    Mono<OrderDto> getCartOrCreateNew();

    Mono<OrderDto> buyItems();

    Mono<Boolean> checkIdExist(Long id);
}