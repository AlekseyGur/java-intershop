package ru.alexgur.intershop.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.alexgur.intershop.item.dto.ItemDto;
import ru.alexgur.intershop.item.dto.ItemNewDto;
import ru.alexgur.intershop.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item fromDto(ItemNewDto itemNewDto);

    Item fromDto(ItemDto itemNewDto);

    @Mapping(target = "quantity", ignore = true)
    ItemDto toDto(Item item);

    default Mono<Item> fromMonoDto(Mono<ItemNewDto> itemNewDto) {
        return itemNewDto.map(this::fromDto);
    }

    default Flux<Item> fromFluxDto(Flux<ItemDto> itemsDto) {
        return itemsDto.map(this::fromDto);
    }

    default Flux<ItemDto> toFluxDto(Flux<Item> itemsDto) {
        return itemsDto.map(this::toDto);
    }
}