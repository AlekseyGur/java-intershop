package ru.alexgur.intershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
@ComponentScan(basePackages = {
        "ru.alexgur.intershop",
        "ru.alexgur.payment"
})
public class MainIntershop {
    public static void main(String[] args) {
        SpringApplication.run(MainIntershop.class, args);
    }
}