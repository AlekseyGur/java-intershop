package ru.alexgur.intershop.item.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.Paging;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.service.ItemService;

@Controller
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/items/{id}")
    public String get(@PathVariable @Positive Long id,
            Model model) {
        model.addAttribute("item", itemService.get(id));
        return "item";
    }

    @GetMapping("/")
    public String getAll(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") SortType sort,
            @RequestParam(defaultValue = "10") @Positive Integer pageSize,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber,
            Model model) {

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<ItemDto> items = itemService.getAll(pageable, search, sort);

        model.addAttribute("items", items);
        model.addAttribute("paging", new Paging(items));
        model.addAttribute("search", search);

        return "main";
    }
}