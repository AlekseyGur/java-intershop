package ru.alexgur.intershop.order.controller;

import java.net.URI;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.Rendering;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
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
                    Flux<ItemDto> items = orderDto.getItems();
                    return Mono.just(Rendering.view("cart")
                            .modelAttribute("items", items.collectList())
                            .modelAttribute("empty", items.hasElements())
                            .modelAttribute("total", orderDto.getTotalSum())
                            .build());
                });
    }

    @PostMapping("/items/{id}")
    public Mono<ServerResponse> updateCartItemQuantity(
            @PathVariable @Positive Long id,
            @RequestParam ActionType action) {

        return orderService.updateCartQuantity(id, action)
                .then(ServerResponse.seeOther(URI.create("/cart"))
                        .build());
    }

    @PostMapping("/buy")
    public Mono<ServerResponse> buyItems(Model model) {
        return orderService.buyItems()
                .flatMap(order -> ServerResponse.seeOther(URI.create("/orders/" + order.getId() + "?newOrder=true"))
                        .build());
    }

}