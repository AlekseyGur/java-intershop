package ru.alexgur.intershop.item.controller;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.Rendering;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.dto.SimplePage;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.service.ItemService;
import ru.alexgur.intershop.system.valid.ValidUUID;
import ru.alexgur.intershop.user.model.CustomUserDetails;

@Controller
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/items/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ServerResponse> createItem(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @Valid @RequestBody Mono<ItemNewDto> item) {
        return itemService.add(item)
                .flatMap(x -> itemService.addCartInfo(x, userDetails.getUserId()))
                .then(ServerResponse.seeOther(URI.create("/"))
                        .build());
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> getItem(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable @ValidUUID UUID id) {
        return itemService.get(id)
                .flatMap(x -> itemService.addCartInfo(x, userDetails.getUserId()))
                .map(data -> Rendering.view("item")
                .modelAttribute("item", data)
                .build());
    }

    @GetMapping
    public Mono<Rendering> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails,
                    @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") SortType sort,
            @RequestParam(defaultValue = "10") @Positive Integer pageSize,
            @RequestParam(defaultValue = "1") @Positive Integer pageNumber) {

            Mono<SimplePage<ItemDto>> page = itemService.getAll(pageNumber - 1,
                            pageSize, search, sort).flatMap(x -> itemService.addCartInfo(x, userDetails.getUserId()));

        return page.map(data -> Rendering.view("main")
                        .modelAttribute("items", Flux.fromIterable(data.getContent()).buffer(3))
                .modelAttribute("paging", data)
                .modelAttribute("search", search)
                .modelAttribute("sort", sort.toString())
                .build());
    }
}