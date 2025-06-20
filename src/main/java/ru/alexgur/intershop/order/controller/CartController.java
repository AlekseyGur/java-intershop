package ru.alexgur.intershop.order.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.reactive.result.view.Rendering;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.order.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final OrderService orderService;

    @GetMapping
    public Mono<Rendering> getCartItems() {
        return orderService.getCartOrCreateNew()
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
    public Mono<String> updateCartItemQuantity(
            @PathVariable @Positive Long id,
            @RequestPart("action") String action) {
        return orderService.updateCartQuantity(id, action)
                .thenReturn("redirect:/cart");
    }

    @PostMapping("/buy")
    public Mono<String> buyItems() {
        return orderService.buyItems()
                .thenReturn("redirect:/orders");
    }

}