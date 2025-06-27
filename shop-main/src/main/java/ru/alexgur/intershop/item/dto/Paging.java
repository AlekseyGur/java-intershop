package ru.alexgur.intershop.item.dto;

import org.springframework.data.domain.Page;

public class Paging {
    private Page<?> page;

    public Paging(Page<?> page) {
        this.page = page;
    }

    public int pageNumber() {
        return page.getNumber() + 1;
    }

    public int pageSize() {
        return page.getSize();
    }

    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    public boolean hasNext() {
        return page.hasNext();
    }
}
