package ru.alexgur.intershop.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class SimplePage<T> implements Serializable {

    private final List<T> content;
    private final Long totalElements;
    private final int number;
    private final int size;
    private final boolean hasPrevious;
    private final boolean hasNext;

    @JsonCreator
    public SimplePage(@JsonProperty("content") List<T> content,
            @JsonProperty("totalElements") Long totalElements,
            @JsonProperty("pageNumber") int number,
            @JsonProperty("pageSize") int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.number = number;
        this.size = size;
        this.hasPrevious = number > 0;
        this.hasNext = ((number + 1) * size) < totalElements;
    }

    public List<T> getContent() {
        return content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public int pageNumber() {
        return number;
    }

    public int pageSize() {
        return size;
    }

    public boolean hasPrevious() {
        return hasPrevious;
    }

    public boolean hasNext() {
        return hasNext;
    }
}