package ru.alexgur.intershop.order.controller;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.ActionType;
import ru.alexgur.intershop.order.dto.OrderDto;
import ru.alexgur.intershop.order.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final OrderService orderService;

    @GetMapping
    public String getCartItems(Model model) {
        OrderDto cart = orderService.getCartOrCreateNew();
        List<ItemDto> items = cart.getItems();
        model.addAttribute("items", items);
        model.addAttribute("empty", items.isEmpty());
        model.addAttribute("total", cart.getTotalSum());

        return "cart";
    }

    @PostMapping("/items/{id}")
    public String updateCartItemQuantity(@PathVariable @Positive Long id,
            @RequestParam String action) {
        ActionType actionEntity = ActionType.valueOf(action.toUpperCase(Locale.ENGLISH));
        orderService.updateCartQuantity(id, actionEntity);

        return "redirect:/cart";
    }

    @PostMapping("/buy")
    public String buyItems(Model model) {
        OrderDto order = orderService.buyItems();
        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

}