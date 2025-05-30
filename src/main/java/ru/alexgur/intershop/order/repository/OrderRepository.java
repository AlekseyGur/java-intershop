package ru.alexgur.intershop.order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.alexgur.intershop.order.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByIsPaidFalseOrderByCreatedAtDesc();

    @Query("UPDATE Order o SET o.isPaid = true WHERE o.id = :orderId")
    void setIsPaid(@Param("orderId") Long orderId);
}