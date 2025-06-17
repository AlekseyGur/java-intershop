package ru.alexgur.intershop.order.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.item.repository.ItemRepository;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.dto.OrderItemDto;
import ru.alexgur.intershop.order.model.Order;
import ru.alexgur.intershop.order.mapper.OrderItemMapper;
import ru.alexgur.intershop.order.mapper.OrderMapper;
import ru.alexgur.intershop.order.model.OrderItem;
import ru.alexgur.intershop.order.repository.OrderItemsRepository;
import ru.alexgur.intershop.order.repository.OrderRepository;
import ru.alexgur.intershop.system.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private static final int MAX_ITEMS_QUANTITY = 100;
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDto> getAll() {
        return orderRepository.findAllByIsPaidTrue()
                .map(orderMapper::toDto)
                .map(this::loadItemQuantity)
                .map(this::calculateTotalSum);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDto> get(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toDto)
                .map(this::loadItemQuantity)
                .map(this::calculateTotalSum)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")));
    }

    public Mono<OrderItemDto> addItemToOrder(Long orderId, Long itemId, Integer quantity) {
        return addItemToOrderImpl(orderId, itemId, quantity)
                .map(orderItemMapper::toDto);
    }

    public Mono<Void> removeItemFromOrder(Long orderId, Long orderItemId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")))
                .flatMap(foundOrder -> {
                    if (foundOrder.getIsPaid()) {
                        return Mono.error(new IllegalStateException("Заказ уже оплачен, редактирование невозможно"));
                    }
                    return orderItemRepository.deleteById(orderItemId);
                });
    }

    @Transactional
    public Mono<OrderDto> buyItems() {
        return getCartOrCreateNew()
                .flatMap(this::validateOrderIsNotEmpty)
                .flatMap(this::validateOrderIsPaid)
                .flatMap(this::setOrderPaid);
    }

    private Mono<OrderDto> validateOrderIsNotEmpty(OrderDto order) {
        return Mono.just(order)
                .filter(o -> !o.getItems().isEmpty())
                .switchIfEmpty(
                        Mono.error(new IllegalStateException("Заказ пустой")));
    }

    private Mono<OrderDto> validateOrderIsPaid(OrderDto order) {
        return Mono.just(order)
                .filter(o -> !o.getIsPaid())
                .switchIfEmpty(
                        Mono.error(new IllegalStateException("Заказ уже оплачен")));
    }

    private Mono<OrderDto> setOrderPaid(OrderDto order) {
        return orderRepository.setIsPaid(order.getId())
                .thenReturn(order);
    }

    @Transactional(readOnly = true)
    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")));
    }

    public Mono<OrderDto> getCartOrCreateNew() {
        return orderRepository.findFirstByIsPaidFalseOrderByIdDesc()
                .map(orderMapper::toDto)
                .map(this::loadItemQuantity)
                .map(this::calculateTotalSum)
                .switchIfEmpty(createOrder());
    }

    public Mono<OrderDto> createOrder() {
        return orderRepository.save(new Order()).map(orderMapper::toDto);
    }

    @Override
    public Mono<Boolean> checkIdExist(Long id) {
        return orderRepository.existsById(id);
    }

    @Override
    @Transactional
    public Mono<Void> updateCartQuantity(Long itemId, ActionType action) {
        return getCartOrCreateNew()
                .flatMap(order -> findOrCreateOrderItem(order, itemId))
                .flatMap(orderItem -> {
                    switch (action) {
                        case PLUS -> updateQuantityPlus(orderItem);
                        case MINUS -> updateQuantityMinus(orderItem);
                        case DELETE -> deleteOrderItem(orderItem);
                        default -> Mono.error(
                                new IllegalArgumentException("Неверное действие с количеством товара"));
                    }
                    return Mono.empty();
                });
    }

    private Mono<OrderItem> findOrCreateOrderItem(OrderDto order, Long itemId) {
        return orderItemRepository.findByOrderIdAndItemId(order.getId(), itemId)
                .switchIfEmpty(addItemToOrderImpl(order.getId(), itemId, 0));
    }

    private Mono<Void> updateQuantityPlus(OrderItem orderItem) {
        return Mono.just(orderItem)
                .filter(item -> item.getQuantity() < MAX_ITEMS_QUANTITY)
                .doOnNext(item -> item.setQuantity(item.getQuantity() + 1))
                .flatMap(orderItemRepository::save)
                .then();
    }

    private Mono<Void> updateQuantityMinus(OrderItem orderItem) {
        return Mono.just(orderItem)
                .filter(item -> item.getQuantity() > 1)
                .doOnNext(item -> item.setQuantity(item.getQuantity() - 1))
                .flatMap(orderItemRepository::save)
                .switchIfEmpty(Mono.fromRunnable(() -> orderItemRepository.delete(orderItem)))
                .then();
    }

    private Mono<Void> deleteOrderItem(OrderItem orderItem) {
        return Mono.fromRunnable(() -> orderItemRepository.delete(orderItem));
    }

    private Mono<OrderItem> addItemToOrderImpl(Long orderId, Long itemId, Integer quantity) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Заказ не найден")))
                .flatMap(order -> {
                    if (order.getIsPaid()) {
                        return Mono.error(new IllegalStateException("Заказ уже оплачен, редактирование невозможно"));
                    }

                    return itemRepository.findById(itemId)
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Товар не найден")))
                            .flatMap(item -> {
                                OrderItem orderItem = new OrderItem();
                                orderItem.setOrder(order);
                                orderItem.setItem(item);
                                orderItem.setQuantity(quantity);
                                return orderItemRepository.save(orderItem);
                            });
                });
    }

    private OrderDto calculateTotalSum(OrderDto order) {
        List<ItemDto> items = order.getItems();
        Double totalSum = items.isEmpty() ? 0.0
                : items.stream()
                        .mapToDouble(x -> x.getPrice() * x.getQuantity())
                        .sum();
        order.setTotalSum(totalSum);
        return order;
    }

    private OrderDto loadItemQuantity(OrderDto order) {
        List<Long> itemsIds = order.getItems().stream().map(ItemDto::getId).toList();
        List<OrderItem> items = orderItemRepository.findAllByItemIdsAndOrderId(itemsIds, order.getId());

        Map<Long, Integer> quantByItemId = items.stream()
                .collect(Collectors.toMap(
                        OrderItem::getId,
                        OrderItem::getQuantity));

        order.getItems().forEach(item -> item.setQuantity(quantByItemId.getOrDefault(item.getId(), 0)));
        return order;
    }
}