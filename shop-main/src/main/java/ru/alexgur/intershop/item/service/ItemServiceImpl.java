package ru.alexgur.intershop.item.service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.dto.SimplePage;
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
    private final CacheManager cacheManager;

    @Override
    public Mono<ItemDto> add(Mono<ItemNewDto> dto) {
        return dto.map(ItemMapper::fromDto)
                .flatMap(itemRepository::save)
                .map(ItemMapper::toDto);
    }

    @Override
    @Cacheable(value = "items", key = "#id.toString()")
    public Mono<ItemDto> get(UUID id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Товар с таким id не найден"))).map(ItemMapper::toDto);
    }

    @Override
    public Mono<Boolean> checkIdExist(UUID itemId) {
        return itemRepository.existsById(itemId);
    }

    @Override
    public Mono<SimplePage<ItemDto>> getAll(Integer pageNumber, Integer pageSize, String search, SortType sort) {
        final int offset = pageNumber * pageSize;
        final int num = pageNumber;
        final int size = pageSize;
        if (sort == null) {
            sort = SortType.ALPHA;
        }

        String cacheKey = "" + pageNumber + ":" + pageSize + ":" + search + ":" + sort;

        ValueWrapper cachedResult = cacheManager.getCache("itemsGetAll").get(cacheKey);

        if (cachedResult != null && cachedResult.get() instanceof SimplePage) {
            return Mono.just((SimplePage<ItemDto>) cachedResult.get());
        }

        Flux<Item> content;
        Mono<Long> totalElements;

        if (search == null || search.isBlank()) {
            totalElements = itemRepository.count();
            if (sort.equals(SortType.ALPHA)) {
                content = itemRepository.findAllLimitOffsetSortedByTitle(pageSize, offset);
            } else {
                content = itemRepository.findAllLimitOffsetSortedByPrice(pageSize, offset);
            }
        } else {
            String searchPattern = "%" + search.strip() + "%";
            totalElements = itemRepository.countBySearchTerm(searchPattern);
            if (sort.equals(SortType.ALPHA)) {
                content = itemRepository.findAllBySearchTermSortedByTitle(searchPattern, pageSize, offset);
            } else {
                content = itemRepository.findAllBySearchTermSortedByPrice(searchPattern, pageSize, offset);
            }
        }

        Mono<SimplePage<ItemDto>> result = Mono.zip(
                content.map(ItemMapper::toDto).collectList(),
                    totalElements
        )
        .map(tuple -> new SimplePage<ItemDto>(
                tuple.getT1(),
                tuple.getT2(),
                num,
                size
        ));

        return result.doOnSuccess(x -> {
            cacheManager.getCache("itemsGetAll").put(cacheKey, x);
        });
    }

    @Override
    public Mono<ItemDto> addCartInfo(ItemDto dto, UUID userId) {
        return orderService.getCart(userId).map(cart -> {
            return cart.getItems();
        }).defaultIfEmpty(Collections.emptyList()).flatMap(items -> {

            Map<UUID, Integer> quantByItemId = items.stream()
                    .collect(Collectors.toMap(ItemDto::getId, ItemDto::getQuantity));

            long count = quantByItemId.getOrDefault(dto.getId(), 0);

            ItemDto newDto = new ItemDto();
            BeanUtils.copyProperties(dto, newDto);
            newDto.setQuantity(count > 0 ? (int) count : 0);
            return Mono.just(newDto);
        });
    }

    public Mono<SimplePage<ItemDto>> addCartInfo(SimplePage<ItemDto> page, UUID userId) {
        var updatedContent = Flux.fromIterable(page.getContent())
                .flatMap(x -> addCartInfo(x, userId))
                .collectList();

        return Mono.zip(updatedContent, Mono.just(page.getTotalElements()))
                .map(tuple -> new SimplePage<>(tuple.getT1(), tuple.getT2(), page.pageNumber(), page.pageSize()));
    }

}