package ru.alexgur.intershop.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.mapper.ItemMapper;
import ru.alexgur.intershop.item.model.Item;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.repository.ItemRepository;
import ru.alexgur.intershop.system.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemDto add(ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long id) {
        return itemRepository.findById(id)
                .map(ItemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Товар с таким id не найдена"));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkIdExist(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDto> getAll(Pageable pageable, String search, SortType sort) {

        Sort jpaSort = switch (sort) {
            case NO -> Sort.unsorted();
            case ALPHA -> Sort.by("title");
            case PRICE -> Sort.by("price");
            default -> throw new IllegalArgumentException("Неизвестный тип сортировки");
        };

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), jpaSort);

        Page<Item> res;
        if (search == null) {
            res = itemRepository.getAll(pageable);
        } else {
            res = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(pageable, search);
        }

        return ItemMapper.toDto(res);
    }
}