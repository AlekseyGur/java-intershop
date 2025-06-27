package ru.alexgur.intershop.system.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<ErrorResponse>> constraintViolationException(final ConstraintViolationException e) {
        log.error("Ошибка сервера. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 400);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<ErrorResponse>> methodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        log.error("Ошибка сервера. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 400);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<ErrorResponse>> internalServerException(final InternalServerException e) {
        log.error("Ошибка сервера. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 500);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ResponseEntity<ErrorResponse>> conditionsNotMetException(final ConditionsNotMetException e) {
        log.error("Условия не выполнены. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 500);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ResponseEntity<ErrorResponse>> duplicatedDataException(final DuplicatedDataException e) {
        log.error("Добавление данных. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 409);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ResponseEntity<ErrorResponse>> notFoundException(final NotFoundException e) {
        log.error("Ресурс не найден. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 404);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ResponseEntity<ErrorResponse>> validationException(final ValidationException e) {
        log.error("Неверный запрос. " + e.getMessage());

        ErrorResponse error = new ErrorResponse(e.getMessage(), 400);
        return Mono.just(ResponseEntity.badRequest().body(error));
    }
}