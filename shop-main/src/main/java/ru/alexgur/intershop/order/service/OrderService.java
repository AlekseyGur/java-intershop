package ru.alexgur.intershop.order.service;

import java.util.UUID;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.dto.OrderItemDto;

public interface OrderService {
    Flux<OrderDto> getAll(UUID userId);

    Mono<OrderDto> get(UUID id, UUID userId);

    Mono<Void> updateCartQuantity(UUID itemId, String action, UUID userId);

    Mono<OrderDto> getCart(UUID userId);

    Mono<OrderDto> buyItems(UUID userId);

    Mono<Boolean> checkIdExist(UUID id);

    Mono<Void> removeItemFromOrder(UUID orderId, UUID orderItemId, UUID userId);

    Mono<OrderItemDto> addItemToOrder(UUID orderId, UUID itemId, Integer quantity, UUID userId);

    Mono<OrderDto> getCartOrCreateNew(UUID userId);
}