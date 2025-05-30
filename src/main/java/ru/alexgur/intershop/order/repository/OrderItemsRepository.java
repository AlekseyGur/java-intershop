package ru.alexgur.intershop.order.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.alexgur.intershop.order.model.OrderItem;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    Optional<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);

    void deleteByOrderIdAndItemId(Long orderId, Long itemId);

    @Modifying
    @Query("UPDATE OrderItem o SET o.quantity = :quantity " +
            "WHERE o.order.id = :orderId AND o.item.id = :itemId")
    void updateQuantityByOrderIdAndItemId(
            @Param("orderId") Long orderId,
            @Param("itemId") Long itemId,
            @Param("quantity") Integer quantity);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.item.id IN (:itemIds)")
    List<OrderItem> findAllByItemIdsAndOrderId(@Param("itemIds") Collection<Long> itemIds,
                    @Param("orderId") Long orderId);
}