package ru.alexgur.intershop.order.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.model.OrderItem;

@Repository
public interface OrderItemsRepository extends R2dbcRepository<OrderItem, Long> {

    @Query("SELECT item_id AS key, SUM(quantity) AS value FROM order_items WHERE item_id IN(:itemIds) AND order_id=:orderId GROUP BY item_id")
    Mono<Map<Long, Integer>> findQuantitiesByItemIdInAndOrderId(List<Long> itemIds, Long orderId);

    @Query("SELECT OrderItem e FROM order_items WHERE e.order_id=:orderId")
    Flux<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT OrderItem e FROM order_items WHERE e.order_id=:orderId AND e.item_id=:itemId LIMIT 1")
    Mono<OrderItem> findByOrderIdAndItemId(Long orderId, Long itemId);

    @Query("DELETE FROM order_items WHERE order_id=:orderId AND item_id=:itemId")
    Mono<Void> deleteByOrderIdAndItemId(Long orderId, Long itemId);

    @Modifying
    @Query("UPDATE OrderItem o SET o.quantity = :quantity " +
            "WHERE o.order.id = :orderId AND o.item.id = :itemId")
    Mono<Void> updateQuantityByOrderIdAndItemId(
            @Param("orderId") Long orderId,
            @Param("itemId") Long itemId,
            @Param("quantity") Integer quantity);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.item.id IN (:itemIds)")
    Flux<OrderItem> findAllByItemIdAndOrderId(@Param("itemIds") List<Long> itemIds,
            @Param("orderId") Long orderId);
}