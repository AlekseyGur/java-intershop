package ru.alexgur.intershop.item.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemDto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 255)
    private String imgPath;

    @Column(nullable = false)
    @Min(value = 0, message = "Количество должно быть положительным")
    private Integer count = 0;

    @Column(nullable = false)
    @Min(value = 0, message = "Цена должна быть положительной")
    private Double price;
}
