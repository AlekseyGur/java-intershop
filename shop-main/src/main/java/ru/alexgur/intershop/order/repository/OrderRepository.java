package ru.alexgur.intershop.order.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.model.Order;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, UUID> {
    @Query("SELECT * FROM orders WHERE is_paid = false AND user_id = :userId ORDER BY id DESC LIMIT 1")
    Mono<Order> findFirstByIsPaidFalseAndUserIdOrderByIdDesc(@Param("userId") UUID userId);

    @Query("SELECT * FROM orders WHERE is_paid = true AND user_id = :userId ORDER BY id DESC")
    Flux<Order> findAllByIsPaidTrueAndUserId(@Param("userId") UUID userId);

    @Query("UPDATE orders SET is_paid = true WHERE id = :orderId")
    Mono<Order> isPaidTrue(@Param("orderId") UUID orderId);

    @Query("SELECT * FROM orders WHERE id = :orderId AND user_id = :userId LIMIT 1")
    Mono<Order> findByIdAndUserId(@Param("orderId") UUID orderId, @Param("userId") UUID userId);
}