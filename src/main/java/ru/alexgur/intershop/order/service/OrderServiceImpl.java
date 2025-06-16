package ru.alexgur.intershop.order.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.item.model.Item;
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
public class OrderServiceImpl implements OrderService {
    private static final int MAX_ITEMS_QUANTITY = 100;
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public Flux<OrderDto> getAll() {
        return orderRepository.findAllByIsPaidTrue().stream()
                .map(OrderMapper::toDto)
                .map(this::loadItemQuantity)
                .map(this::calculateTotalSum)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<OrderDto> get(Long orderId) {
        return orderRepository.findById(orderId)
                .map(OrderMapper::toDto)
                .map(this::loadItemQuantity)
                .map(this::calculateTotalSum)
                .orElseThrow(() -> new NotFoundException("Заказ не найден"));
    }

    public Mono<OrderItemDto> addItemToOrder(Long orderId, Long itemId, Integer quantity) {
        return OrderItemMapper.toDto(addItemToOrderImpl(orderId, itemId, quantity));
    }

    public Mono<Void> removeItemFromOrder(Long orderId, Long orderItemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        if (order.getIsPaid()) {
            throw new IllegalStateException("Заказ уже оплачен, редактирование невозможно");
        }

        orderItemRepository.deleteById(orderItemId);
    }

    @Transactional
    public Mono<OrderDto> buyItems() {
        OrderDto order = getCartOrCreateNew();

        if (order.getIsPaid()) {
            throw new IllegalStateException("Невозможно оплатить пустой заказ");
        }

        order.setIsPaid(true);
        orderRepository.setIsPaid(order.getId());
        return order;
    }

    @Transactional(readOnly = true)
    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
    }

    public Mono<OrderDto> getCartOrCreateNew() {
        Optional<Order> order = orderRepository.findFirstByIsPaidFalseOrderByIdDesc();
        if (order.isPresent()) {
            return order
                    .map(OrderMapper::toDto)
                    .map(this::loadItemQuantity)
                    .map(this::calculateTotalSum)
                    .get();
        }
        return createOrder();
    }

    public Mono<OrderDto> createOrder() {
        return OrderMapper.toDto(orderRepository.save(new Order()));
    }

    @Override
    public Mono<Boolean> checkIdExist(Long id) {
        return orderRepository.existsById(id);
    }

    @Override
    public Mono<Void> updateCartQuantity(Long itemId, ActionType action) {
        OrderDto order = getCartOrCreateNew();

        OrderItem orderItem = orderItemRepository.findByOrderIdAndItemId(order.getId(), itemId)
                .orElseGet(() -> addItemToOrderImpl(order.getId(), itemId, 0));

        switch (action) {
            case PLUS -> updateQuantityPlus(orderItem);
            case MINUS -> updateQuantityMinus(orderItem);
            case DELETE -> deleteOrderItem(orderItem);
            default -> throw new IllegalArgumentException("Неверное действие с количеством товара");
        }
    }

    private void updateQuantityPlus(OrderItem orderItem) {
        if (orderItem.getQuantity() < MAX_ITEMS_QUANTITY) {
            orderItem.setQuantity(orderItem.getQuantity() + 1);
            orderItemRepository.save(orderItem);
        }
    }

    private void updateQuantityMinus(OrderItem orderItem) {
        if (orderItem.getQuantity() > 1) {
            orderItem.setQuantity(orderItem.getQuantity() - 1);
            orderItemRepository.save(orderItem);
        } else if (orderItem.getQuantity() == 1) {
            orderItemRepository.delete(orderItem);
        }
    }

    private void deleteOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
    }

    private OrderItem addItemToOrderImpl(Long orderId, Long itemId, Integer quantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        if (order.getIsPaid()) {
            throw new IllegalStateException("Заказ уже оплачен, редактирование невозможно");
        }

        Item item = itemRepository.findById(itemId).get();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(quantity);
        orderItemRepository.save(orderItem);
        return orderItemRepository.findByOrderIdAndItemId(order.getId(), itemId).get();
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

    private Mono<OrderDto> loadItemQuantity(OrderDto order) {
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