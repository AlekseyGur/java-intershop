package ru.yandex.practicum.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Mono<String>> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.badRequest()
                .body(Mono.just(ex.getMessage()));
    }
}
