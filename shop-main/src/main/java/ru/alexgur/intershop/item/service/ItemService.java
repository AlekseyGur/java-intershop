package ru.alexgur.intershop.item.service;

import java.util.UUID;

import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.dto.SimplePage;
import ru.alexgur.intershop.item.model.SortType;

public interface ItemService {
    Mono<ItemDto> add(Mono<ItemNewDto> item);

    Mono<SimplePage<ItemDto>> getAll(Integer limit, Integer offset, String search, SortType sort);

    Mono<Boolean> checkIdExist(UUID id);

    Mono<ItemDto> get(UUID id);

    Mono<ItemDto> addCartInfo(ItemDto dto, UUID userId);

    Mono<SimplePage<ItemDto>> addCartInfo(SimplePage<ItemDto> page, UUID userId);
}