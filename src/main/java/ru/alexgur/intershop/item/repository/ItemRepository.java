package ru.alexgur.intershop.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.alexgur.intershop.item.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
        Page<Item> getAll(Pageable pageable);

        Page<Item> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(Pageable pageable, String search);
}