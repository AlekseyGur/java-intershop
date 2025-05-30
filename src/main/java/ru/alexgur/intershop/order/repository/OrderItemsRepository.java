package ru.alexgur.intershop.order.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.alexgur.intershop.order.model.OrderItem;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);

    void deleteByOrderIdAndItemId(Long orderId, Long itemId);

    void updateQuantityByOrderIdAndItemId(Long orderId, Long itemId, Integer quantity);
}