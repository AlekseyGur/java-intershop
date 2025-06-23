package ru.alexgur.intershop.order.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.model.OrderItem;

@Repository
public interface OrderItemsRepository extends R2dbcRepository<OrderItem, UUID> {

    @Query("SELECT item_id AS key, SUM(quantity) AS value FROM order_items WHERE item_id IN(:itemIds) AND order_id=:orderId GROUP BY item_id")
    Mono<Map<UUID, Integer>> findQuantitiesByItemIdInAndOrderId(List<UUID> itemIds, UUID orderId);

    @Query("SELECT * FROM order_items WHERE order_id=:orderId")
    Flux<OrderItem> findByOrderId(UUID orderId);

    @Query("SELECT * FROM order_items WHERE order_id=:orderId AND item_id=:itemId LIMIT 1")
    Mono<OrderItem> findByOrderIdAndItemId(UUID orderId, UUID itemId);

    @Query("DELETE FROM order_items WHERE order_id=:orderId AND item_id=:itemId")
    Mono<Void> deleteByOrderIdAndItemId(UUID orderId, UUID itemId);

    @Query("UPDATE order_items SET quantity = :quantity " +
            "WHERE order_id = :orderId AND item_id = :itemId")
    Mono<Void> quantity(
            @Param("quantity") Integer quantity,
            @Param("orderId") UUID orderId,
            @Param("itemId") UUID itemId);

    @Query("SELECT * FROM order_items " +
            "WHERE order_id = :orderId " +
            "AND item_id IN (:itemIds)")
    Flux<OrderItem> findAllByItemIdsAndOrderId(
            @Param("orderId") UUID orderId,
            @Param("itemIds") List<UUID> itemIds);

    @Query("SELECT * FROM order_items WHERE order_id = :orderId AND item_id IN (:itemIds)")
    Flux<OrderItem> findAllByItemIdInAndOrderId(@Param("itemIds") List<UUID> itemIds,
            @Param("orderId") UUID orderId);
}
