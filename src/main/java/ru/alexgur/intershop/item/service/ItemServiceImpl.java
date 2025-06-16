package ru.alexgur.intershop.item.service;

import java.util.Collections;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.dto.ReactivePage;
import ru.alexgur.intershop.item.mapper.ItemMapper;
import ru.alexgur.intershop.item.model.Item;
import ru.alexgur.intershop.item.model.SortType;
import ru.alexgur.intershop.item.repository.ItemRepository;
import ru.alexgur.intershop.order.service.OrderService;
import ru.alexgur.intershop.system.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final OrderService orderService;

    @Override
    @Transactional
    public Mono<ItemDto> add(Mono<ItemNewDto> dto) {
        return ItemMapper.toMonoItem(dto)
                .flatMap(item -> itemRepository.save(item))
                .map(ItemMapper::toDto);
    }

    @Override
    public Mono<ItemDto> get(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Товар с таким id не найден")))
                .map(ItemMapper::toDto)
                .flatMap(this::addCartInfo);
    }

    @Override
    @Transactional
    public Mono<Boolean> checkIdExist(Long itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    @Transactional
    public Mono<ReactivePage<ItemDto>> getAll(Integer pageNumber, Integer pageSize, String search, SortType sort) {
        final int offset = pageNumber * pageSize;
        final int num = pageNumber;
        final int size = pageSize;

        Mono<Long> totalElements = itemRepository.countBySearchTerm(search);
        Flux<Item> content;

        if (sort.equals(SortType.ALPHA)) {
            content = itemRepository.findAllBySearchTermSortedByTitle(pageSize, offset, search);
        } else {
            content = itemRepository.findAllBySearchTermSortedByPrice(pageSize, offset, search);
        }

        Flux<ItemDto> contentDto = content
                .map(ItemMapper::toDto)
                .flatMap(this::addCartInfo);

        return Mono.zip(contentDto.collectList(), totalElements)
                .map(tuple -> new ReactivePage<>(
                        Flux.fromIterable(tuple.getT1()),
                        Mono.just(tuple.getT2()),
                        num,
                        size))
                .onErrorResume(error -> {
                    return Mono.error(new RuntimeException("Ошибка получения данных"));
                });
    }

    private Mono<ItemDto> addCartInfo(ItemDto dto) {
        return orderService.getCartOrCreateNew()
                .map(cart -> cart.getItems())
                .defaultIfEmpty(Collections.emptyList())
                .map(items -> {
                    long count = items.stream()
                            .filter(i -> i.getId().equals(dto.getId()))
                            .count();
                    dto.setQuantity(count > 0 ? (int) count : 0);
                    return dto;
                });
    }

}