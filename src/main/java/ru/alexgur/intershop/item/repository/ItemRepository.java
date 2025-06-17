package ru.alexgur.intershop.item.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.model.Item;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Query("SELECT * FROM item " +
            "WHERE title ILIKE %:search% " +
            "ORDER BY title " +
            "LIMIT :limit OFFSET :offset")
    Flux<Item> findAllBySearchTermSortedByTitle(
            @Param("limit") Integer limit,
            @Param("offset") Integer offset,
            @Param("search") String search);

    @Query("SELECT * FROM item " +
            "WHERE title ILIKE %:search% " +
            "ORDER BY price " +
            "LIMIT :limit OFFSET :offset")
    Flux<Item> findAllBySearchTermSortedByPrice(
            @Param("limit") Integer limit,
            @Param("offset") Integer offset,
            @Param("search") String search);

    @Query("SELECT COUNT(*) FROM item WHERE title ILIKE %:search%")
    Mono<Long> countBySearchTerm(String search);
}