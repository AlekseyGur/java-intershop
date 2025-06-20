package ru.alexgur.intershop.order.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.dto.OrderItemDto;

public interface OrderService {
    Flux<OrderDto> getAll();

    Mono<OrderDto> get(Long id);

    Mono<Void> updateCartQuantity(Long itemId, String action);

    Mono<OrderDto> getCartOrCreateNew();

    Mono<OrderDto> buyItems();

    Mono<Boolean> checkIdExist(Long id);

    Mono<Void> removeItemFromOrder(Long orderId, Long orderItemId);

    Mono<OrderItemDto> addItemToOrder(Long orderId, Long itemId, Integer quantity);
}