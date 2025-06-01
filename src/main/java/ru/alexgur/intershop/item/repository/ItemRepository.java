package ru.alexgur.intershop.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import ru.alexgur.intershop.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
        @NonNull
        Page<Item> findAll(@NonNull Pageable pageable);

        @Query("SELECT i FROM Item i " +
                        "WHERE i.title ILIKE %:search% " +
                        "OR i.description ILIKE %:search%")
        Page<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Pageable pageable,
                        @Param("search") String search);
}