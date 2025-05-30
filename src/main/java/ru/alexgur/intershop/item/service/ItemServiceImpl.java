package ru.alexgur.intershop.item.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import ru.alexgur.intershop.order.service.OrderService;
import ru.alexgur.intershop.system.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final OrderService orderService;

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
                .map(this::addCartInfo)
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
            res = itemRepository.findAll(pageable);
        } else {
            res = itemRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(pageable, search);
        }

        return addCartInfo(ItemMapper.toDto(res));
    }

    private List<ItemDto> addCartInfo(List<ItemDto> items) {
        Map<Long, Integer> itemsIdsInCart = orderService.getCartOrCreateNew().getItems().stream()
                .collect(Collectors.groupingBy(
                        ItemDto::getId,
                        Collectors.summingInt(x -> 1)));

        return items.stream()
                .map(x -> {
                    x.setQuantity(itemsIdsInCart.getOrDefault(x.getId(), 0));
                    return x;
                }).toList();
    }

    private ItemDto addCartInfo(ItemDto post) {
        return addCartInfo(List.of(post)).get(0);
    }

    private Page<ItemDto> addCartInfo(Page<ItemDto> items) {
        return new PageImpl<>(addCartInfo(items.getContent()),
                items.getPageable(),
                items.getTotalElements());
    }
}