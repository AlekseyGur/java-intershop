package ru.alexgur.intershop.system.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class ErrorController {

    @GetMapping("/error")
    public Mono<Rendering> err(@RequestParam(defaultValue = "") String reason) {
        return Mono.just(Rendering.view("error")
                .modelAttribute("reason", reason)
                .build());
    }
}