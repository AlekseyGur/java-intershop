package ru.alexgur.intershop.order.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.service.OrderService;
import ru.alexgur.intershop.system.valid.ValidUUID;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> updateCartItemQuantity(
            @PathVariable @ValidUUID UUID id,
            @RequestPart("action") String action) {
        return orderService.updateCartQuantity(id, action)
                .thenReturn("redirect:/items/" + id);
    }

    @GetMapping("/orders")
    public Mono<Rendering> getCartItems() {

        Flux<OrderDto> data = orderService.getAll();

        return Mono.just(Rendering.view("orders")
                .modelAttribute("orders", data)
                .build());
    }

    @GetMapping("/orders/{orderId}")
    public Mono<Rendering> getById(@PathVariable @ValidUUID UUID orderId) {
        return orderService.get(orderId).map(data -> Rendering.view("order")
                .modelAttribute("order", data)
                .build());
    }
}