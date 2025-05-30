package ru.alexgur.intershop.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final OrderService orderService;

    @GetMapping("/items")
    public String getCartItems(Model model) {
        model.addAttribute("items", orderService.getCart());
        return "cart";
    }

    @PostMapping("/items/{id}")
    public String updateCartItemQuantity(@PathVariable @Positive Long id,
            @RequestParam ActionType action) {
        orderService.updateCartQuantity(id, action);

        return "redirect:/cart/items";
    }

    @PostMapping("/buy")
    public String buyItems(Model model) {
        OrderDto order = orderService.buyItems();
        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

}