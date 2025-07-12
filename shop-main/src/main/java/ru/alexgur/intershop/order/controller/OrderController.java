package ru.alexgur.intershop.order.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import ru.alexgur.intershop.user.model.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@RequestMapping
public class OrderController {
    private final OrderService orderService;

    @PostMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<String> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
                    @PathVariable @ValidUUID UUID id,
            @RequestPart("action") String action) {
        return orderService.updateCartQuantity(id, action, userDetails.getUserId())
                .thenReturn("redirect:/items/" + id);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<Rendering> getCartItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Flux<OrderDto> data = orderService.getAll(userDetails.getUserId());

        return Mono.just(Rendering.view("orders")
                .modelAttribute("orders", data)
                .build());
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<Rendering> getById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable @ValidUUID UUID orderId) {
        return orderService.get(orderId, userDetails.getUserId()).map(data -> Rendering.view("order")
                .modelAttribute("order", data)
                .build());
    }
}