package ru.alexgur.intershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = { "ru.alexgur.intershop" })
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}