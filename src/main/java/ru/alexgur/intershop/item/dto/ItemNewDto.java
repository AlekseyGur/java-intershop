package ru.alexgur.intershop.item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemNewDto {
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String imgPath;

    @Min(value = 0, message = "Количество должно быть неотрицательным")
    private Integer quantity = 0;

    @Min(value = 0, message = "Цена должна быть неотрицательной")
    private Double price;
}
