package ru.alexgur.intershop.item.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemDto {
    private UUID id;

    private String title;

    @Size(max = 1000)
    private String description;

    @Size(max = 255)
    private String imgPath;

    @Min(value = 0, message = "Количество должно быть положительным")
    private Integer quantity = 0;

    @Min(value = 0, message = "Цена должна быть положительной")
    private Double price;
}
