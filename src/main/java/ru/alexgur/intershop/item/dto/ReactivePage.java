package ru.alexgur.intershop.item.dto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactivePage<T> {

    private final Flux<T> content;
    private final Mono<Long> totalElements;
    private final int number;
    private final int size;
    private final boolean hasPrevious;
    private final Mono<Boolean> hasNext;

    public ReactivePage(Flux<T> content, Mono<Long> totalElements, int number, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.number = number;
        this.size = size;
        this.hasPrevious = number > 0;
        this.hasNext = totalElements.defaultIfEmpty(-1L)
                .map(total -> ((number + 1) * size) < total);
    }

    public Flux<T> getContent() {
        return content;
    }

    public Mono<Long> getTotalElements() {
        return totalElements;
    }

    public int getNumber() {
        return number;
    }

    public int getSize() {
        return size;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public Mono<Boolean> isHasNext() {
        return hasNext;
    }
}