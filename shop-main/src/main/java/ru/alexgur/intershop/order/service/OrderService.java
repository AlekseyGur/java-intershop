package ru.alexgur.intershop.order.service;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.dto.OrderItemDto;

public interface OrderService {
    Flux<OrderDto> getAll();

    Mono<OrderDto> get(UUID id);

    Mono<Void> updateCartQuantity(UUID itemId, String action);

    Mono<OrderDto> getCart();

    Mono<OrderDto> buyItems();

    Mono<Boolean> checkIdExist(UUID id);

    Mono<Void> removeItemFromOrder(UUID orderId, UUID orderItemId);

    Mono<OrderItemDto> addItemToOrder(UUID orderId, UUID itemId, Integer quantity);

    Mono<OrderDto> getCartOrCreateNew();
}