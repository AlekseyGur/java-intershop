package ru.alexgur.intershop.item.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 100)
    @NotBlank(message = "Название товара не может быть пустым")
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "img_path", nullable = false, length = 255)
    @NotBlank(message = "Путь к изображению не может быть пустым")
    private String imgPath;

    @Column(name = "price", nullable = false)
    @Min(value = 0, message = "Цена должна быть положительной")
    @NotNull(message = "Цена не может быть пустой")
    private Double price;
}
