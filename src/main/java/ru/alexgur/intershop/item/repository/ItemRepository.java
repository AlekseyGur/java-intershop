package ru.alexgur.intershop.item.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.model.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    Flux<Item> findAllBySearchTermSortedByTitle(
            @Param("limit") Integer limit,
            @Param("offset") Integer offset,
            @Param("search") String search);

    Flux<Item> findAllBySearchTermSortedByPrice(
            @Param("limit") Integer limit,
            @Param("offset") Integer offset,
            @Param("search") String search);

    Mono<Long> countBySearchTerm(String search);
}