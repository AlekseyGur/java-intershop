package ru.alexgur.intershop.item.service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

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
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final OrderService orderService;
    private final ItemMapper itemMapper;

    @Override
    public Mono<ItemDto> add(Mono<ItemNewDto> dto) {
        return dto
                .map(itemMapper::fromDto)
                .flatMap(itemRepository::save)
                .map(itemMapper::toDto);
    }

    @Override
    public Mono<ItemDto> get(UUID id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Товар с таким id не найден")))
                .map(itemMapper::toDto);
    }

    @Override
    public Mono<Boolean> checkIdExist(UUID itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    public Mono<ReactivePage<ItemDto>> getAll(Integer pageNumber, Integer pageSize, String search, SortType sort) {
        final int offset = pageNumber * pageSize;
        final int num = pageNumber;
        final int size = pageSize;
        if (sort == null) {
            sort = SortType.ALPHA;
        }

        String searchPattern;
        Flux<Item> content;
        Mono<Long> totalElements;

        if (search == null) {
            totalElements = itemRepository.count();
            if (sort.equals(SortType.ALPHA)) {
                content = itemRepository.findAllLimitOffsetSortedByTitle(pageSize, offset);
            } else {
                content = itemRepository.findAllLimitOffsetSortedByPrice(pageSize, offset);
            }
        } else {
            searchPattern = "%" + search.strip() + "%";
            totalElements = itemRepository.countBySearchTerm(searchPattern);

            if (sort.equals(SortType.ALPHA)) {
                content = itemRepository.findAllBySearchTermSortedByTitle(searchPattern, pageSize, offset);
            } else {
                content = itemRepository.findAllBySearchTermSortedByPrice(searchPattern, pageSize, offset);
            }
        }
        Flux<ItemDto> contentDto = content
                .map(itemMapper::toDto);

        return Mono.zip(contentDto.collectList(), totalElements)
                .map(tuple -> new ReactivePage<ItemDto>(
                Flux.fromIterable(tuple.getT1()),
                Mono.just(tuple.getT2()),
                num,
                size));
    }

    @Override
    public Mono<ItemDto> addCartInfo(ItemDto dto) {
        return orderService.getCart()
                .map(cart -> {
                    return cart.getItems();
                })
                .defaultIfEmpty(Collections.emptyList())
                .flatMap(items -> {

                    Map<UUID, Integer> quantByItemId = items.stream()
                            .collect(Collectors.toMap(ItemDto::getId, ItemDto::getQuantity));

                    long count = quantByItemId.getOrDefault(dto.getId(), 0);

                    ItemDto newDto = new ItemDto();
                    BeanUtils.copyProperties(dto, newDto);
                    newDto.setQuantity(count > 0 ? (int) count : 0);
                    return Mono.just(newDto);
                });
    }

    public Mono<ReactivePage<ItemDto>> addCartInfo(ReactivePage<ItemDto> page) {
        return Mono.just(page).flatMap(x -> {
            return Mono.just(
                    new ReactivePage<ItemDto>(
                            x.getContent().flatMap(this::addCartInfo),
                            x.getTotalElements(),
                            x.pageNumber(),
                            x.pageSize()));
        });
    }

}