package ru.alexgur.intershop.order.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.mapper.ItemMapper;
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
import ru.alexgur.intershop.system.exception.ValidationException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final int MAX_ITEMS_QUANTITY = 100;
    private final OrderRepository orderRepository;
    private final OrderItemsRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ItemMapper itemMapper;

    @Override
    public Flux<OrderDto> getAll() {
        return orderRepository.findAllByIsPaidTrue()
                .flatMap(this::convertOrderToOrderDto);
    }

    @Override
    public Mono<OrderDto> get(Long orderId) {
        return orderRepository.findById(orderId)
                .flatMap(this::convertOrderToOrderDto)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")));
    }

    @Override
    public Mono<OrderItemDto> addItemToOrder(Long orderId, Long itemId, Integer quantity) {
        return addItemToOrderImpl(orderId, itemId, quantity)
                .map(orderItemMapper::toDto);
    }

    @Override
    public Mono<Void> removeItemFromOrder(Long orderId, Long orderItemId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")))
                .flatMap(foundOrder -> {
                    if (foundOrder.getIsPaid()) {
                        return Mono.error(new ValidationException("Заказ уже оплачен, редактирование невозможно"));
                    }
                    return orderItemRepository.deleteById(orderItemId);
                });
    }

    @Override
    public Mono<OrderDto> buyItems() {
        return getCartOrCreateNew()
                .flatMap(this::validateOrderIsNotEmpty)
                .flatMap(this::validateOrderIsPaid)
                .flatMap(this::setOrderPaid);
    }

    @Override
    public Mono<Boolean> checkIdExist(Long id) {
        return orderRepository.existsById(id);
    }

    @Override
    public Mono<Void> updateCartQuantity(Long itemId, String action) {
        return getCartOrCreateNew()
                .flatMap(order -> findOrCreateOrderItem(order, itemId))
                .flatMap(orderItem -> {
                    return switch (action.toUpperCase()) {
                        case "PLUS" -> updateQuantityPlus(orderItem);
                        case "MINUS" -> updateQuantityMinus(orderItem);
                        case "DELETE" -> deleteOrderItem(orderItem);
                        default -> Mono.error(
                                new NotFoundException("Неверное действие с количеством товара"));
                    };
                });
    }

    private Mono<Void> updateQuantityPlus(OrderItem orderItem) {
        if (orderItem.getQuantity() < MAX_ITEMS_QUANTITY) {
            orderItem.setQuantity(orderItem.getQuantity() + 1);
            return orderItemRepository.quantity(
                    orderItem.getQuantity(),
                    orderItem.getOrderId(),
                    orderItem.getItemId());
        }
        return Mono.empty();
    }

    private Mono<Void> updateQuantityMinus(OrderItem orderItem) {
        if (orderItem.getQuantity() > 1) {
            orderItem.setQuantity(orderItem.getQuantity() - 1);
            return orderItemRepository.save(orderItem).then();
        } else if (orderItem.getQuantity() == 1) {
            return orderItemRepository.delete(orderItem);
        }
        return Mono.empty();
    }

    private Mono<Void> deleteOrderItem(OrderItem orderItem) {
        orderItemRepository.delete(orderItem);
        return Mono.empty();
    }

    private Mono<OrderDto> validateOrderIsNotEmpty(OrderDto order) {
        return Mono.just(order)
                .filter(o -> !o.getItems().isEmpty())
                .switchIfEmpty(
                        Mono.error(new ValidationException("Заказ пустой")));
    }

    private Mono<OrderDto> validateOrderIsPaid(OrderDto order) {
        return Mono.just(order)
                .filter(o -> !o.getIsPaid())
                .switchIfEmpty(
                        Mono.error(new ValidationException("Заказ уже оплачен")));
    }

    private Mono<OrderDto> setOrderPaid(OrderDto order) {
        return orderRepository.isPaidTrue(order.getId())
                .thenReturn(order);
    }

    public Mono<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")));
    }

    public Mono<OrderDto> getCartOrCreateNew() {
        return orderRepository.findFirstByIsPaidFalseOrderByIdDesc()
                .flatMap(this::convertOrderToOrderDto)
                .switchIfEmpty(createOrder());
    }

    public Mono<OrderDto> createOrder() {
        return orderRepository.save(new Order()).map(orderMapper::toDto);
    }

    private Mono<OrderItem> findOrCreateOrderItem(OrderDto order, Long itemId) {
        return orderItemRepository.findByOrderIdAndItemId(order.getId(), itemId)
                .switchIfEmpty(addItemToOrderImpl(order.getId(), itemId, 0));
    }

    private Mono<OrderItem> addItemToOrderImpl(Long orderId, Long itemId, Integer quantity) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new NotFoundException("Заказ не найден")))
                .flatMap(order -> {
                    if (order.getIsPaid()) {
                        return Mono.error(new ValidationException("Заказ уже оплачен, редактирование невозможно"));
                    }

                    return itemRepository.findById(itemId)
                            .switchIfEmpty(Mono.error(new NotFoundException("Товар не найден")))
                            .flatMap(item -> {
                                OrderItem orderItem = new OrderItem();
                                orderItem.setOrderId(orderId);
                                orderItem.setItemId(item.getId());
                                orderItem.setQuantity(quantity);
                                return orderItemRepository.save(orderItem);
                            });
                });
    }

    private Mono<OrderDto> convertOrderToOrderDto(Order order) {
        Flux<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        Mono<List<Long>> itemsIds = items.map(OrderItem::getItemId).collectList();

        Mono<Map<Long, Integer>> quantByItemId = items.collectMap(OrderItem::getItemId, OrderItem::getQuantity);

        Mono<List<ItemDto>> itemsDtos = itemsIds.flatMap(
                ids -> {
                    if (ids.isEmpty()) {
                        return Mono.just(List.of());
                    }
                    return itemRepository.findAllByIdIn(ids).map(itemMapper::toDto).collectList();
                });

        return Mono.zip(Mono.just(order), itemsDtos, quantByItemId).map(tuple -> {
            Order originalOrder = tuple.getT1();
            List<ItemDto> itemsList = tuple.getT2();
            Map<Long, Integer> quantities = tuple.getT3();


            OrderDto dto = new OrderDto();
            BeanUtils.copyProperties(originalOrder, dto);

            if (!itemsList.isEmpty()) {
                itemsList.forEach(x -> x.setQuantity(quantities.getOrDefault(x.getId(), 0)));
                dto.setItems(itemsList);
                dto.setTotalSum(itemsList.isEmpty() ? 0.0
                        : itemsList.stream()
                                .mapToDouble(x -> x.getPrice() * x.getQuantity())
                                .sum());
            }
            return dto;
        });
    }
}