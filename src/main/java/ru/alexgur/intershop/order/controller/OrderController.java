package ru.alexgur.intershop.order.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.service.OrderService;
import ru.alexgur.intershop.system.valid.ValidUUID;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public Mono<Rendering> getCartItems() {

        Flux<OrderDto> data = orderService.getAll();

        return Mono.just(Rendering.view("orders")
                .modelAttribute("orders", data)
                .build());
    }

    @GetMapping("/{orderId}")
    public Mono<Rendering> getById(@PathVariable @ValidUUID UUID orderId) {
        return orderService.get(orderId).map(data -> Rendering.view("order")
                .modelAttribute("order", data)
                .build());
    }
}