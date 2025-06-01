package ru.alexgur.intershop.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.model.SortType;

public interface ItemService {
    ItemDto add(ItemDto item);

    Page<ItemDto> getAll(Pageable pageable, String search, SortType sort);

    boolean checkIdExist(Long id);

    ItemDto get(Long id);
}