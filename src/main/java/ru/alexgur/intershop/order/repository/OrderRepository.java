package ru.alexgur.intershop.order.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.model.Order;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {
    @Query("SELECT * FROM orders WHERE is_paid = false ORDER BY id DESC LIMIT 1")
    Mono<Order> findFirstByIsPaidFalseOrderByIdDesc();

    Flux<Order> findAllByIsPaidTrue();

    @Query(value = "UPDATE orders SET is_paid = true WHERE id = :orderId")
    Mono<Void> isPaidTrue(@Param("orderId") Long orderId);
}