package ru.alexgur.intershop.order.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.model.Order;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {
    Mono<Order> findFirstByIsPaidFalseOrderByIdDesc();

    Flux<Order> findAllByIsPaidTrue();

    @Modifying
    @Query("UPDATE Order o SET o.isPaid = true WHERE o.id = :orderId")
    Mono<Void> setIsPaid(@Param("orderId") Long orderId);
}