package ru.alexgur.intershop.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.alexgur.intershop.order.service.OrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/orders")
@Validated
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("orders", orderService.getAll());
        return "orders";
    }

    @GetMapping("/{orderId}")
    public String getById(@PathVariable @Positive Long orderId, Model model) {
        model.addAttribute("order", orderService.get(orderId));
        return "order";
    }

}
