package ru.alexgur.intershop.item.mapper;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item fromDto(ItemDto itemDto) {
        if (itemDto == null) {
            return null;
        }
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setImgPath(itemDto.getImgPath());
        item.setPrice(itemDto.getPrice());
        return item;
    }

    public Item fromDto(ItemNewDto itemNewDto) {
        if (itemNewDto == null) {
            return null;
        }
        Item item = new Item();
        item.setTitle(itemNewDto.getTitle());
        item.setDescription(itemNewDto.getDescription());
        item.setImgPath(itemNewDto.getImgPath());
        item.setPrice(itemNewDto.getPrice());
        return item;
    }

    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setImgPath(item.getImgPath());
        dto.setPrice(item.getPrice());
        return dto;
    }

    public Mono<Item> fromMonoDto(Mono<ItemNewDto> itemNewDto) {
        return itemNewDto.map(ItemMapper::fromDto);
    }

    public Flux<Item> fromFluxDto(Flux<ItemDto> itemsDto) {
        return itemsDto.map(ItemMapper::fromDto);
    }

    public Flux<ItemDto> toFluxDto(Flux<Item> itemsDto) {
        return itemsDto.map(ItemMapper::toDto);
    }
}