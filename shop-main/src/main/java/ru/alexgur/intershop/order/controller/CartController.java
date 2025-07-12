package ru.alexgur.intershop.order.controller;

import java.util.List;
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
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.order.service.OrderService;
import ru.alexgur.intershop.system.valid.ValidUUID;
import ru.alexgur.intershop.user.model.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<Rendering> getCartItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return orderService.getCartOrCreateNew(userDetails.getUserId())
                .flatMap(orderDto -> {
                    List<ItemDto> items = orderDto.getItems();
                    return Mono.just(Rendering.view("cart")
                            .modelAttribute("items", items)
                            .modelAttribute("empty", items.isEmpty())
                            .modelAttribute("total", orderDto.getTotalSum())
                            .build());
                });
    }

    @PostMapping(value = "/items/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<String> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
                    @PathVariable @ValidUUID UUID id,
            @RequestPart("action") String action) {
        return orderService.updateCartQuantity(id, action, userDetails.getUserId())
                .thenReturn("redirect:/cart");
    }

    @PostMapping("/buy")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Mono<String> buyItems(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return orderService.buyItems(userDetails.getUserId())
                .thenReturn("redirect:/orders")
                .onErrorResume(e -> {
                    return Mono.just("redirect:/error?reason=low_balance");
                });
    }
}