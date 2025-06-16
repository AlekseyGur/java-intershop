package ru.alexgur.intershop.item.service;

import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.dto.ReactivePage;
import ru.alexgur.intershop.item.model.SortType;

public interface ItemService {
    Mono<ItemDto> add(Mono<ItemNewDto> item);

    Mono<ReactivePage<ItemDto>> getAll(Integer limit, Integer offset, String search, SortType sort);

    Mono<Boolean> checkIdExist(Long id);

    Mono<ItemDto> get(Long id);
}