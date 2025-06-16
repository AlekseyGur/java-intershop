package ru.alexgur.intershop.item.mapper;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.model.Item;

@UtilityClass
public class ItemMapper {
    public static ItemDto toDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setTitle(item.getTitle());
        itemDto.setDescription(item.getDescription());
        itemDto.setImgPath(item.getImgPath());
        itemDto.setPrice(item.getPrice());
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setImgPath(itemDto.getImgPath());
        item.setPrice(itemDto.getPrice());
        return item;
    }

    public static Item toItem(ItemNewDto itemNewDto) {
        Item item = new Item();
        item.setTitle(itemNewDto.getTitle());
        item.setDescription(itemNewDto.getDescription());
        item.setImgPath(itemNewDto.getImgPath());
        item.setPrice(itemNewDto.getPrice());
        return item;
    }

    public static List<Item> fromDto(List<ItemDto> itemsDto) {
        return itemsDto.stream().map(ItemMapper::toItem).toList();
    }

    public static List<ItemDto> toDto(List<Item> items) {
        return items.stream().map(ItemMapper::toDto).toList();
    }

    public static Page<ItemDto> toDto(Page<Item> posts) {
        return posts.map(ItemMapper::toDto);
    }

    public static Mono<Item> toMonoItem(Mono<ItemNewDto> monoDto) {
        return monoDto.map(ItemMapper::toItem);
    }
}