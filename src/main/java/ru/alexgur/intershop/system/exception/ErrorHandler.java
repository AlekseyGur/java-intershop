package ru.alexgur.intershop.system.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> constraintViolationException(final ConstraintViolationException e) {
        log.error("Ошибка сервера. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 400));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Ошибка сервера. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 400));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> internalServerException(final InternalServerException e) {
        log.error("Ошибка сервера. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 500));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> conditionsNotMetException(final ConditionsNotMetException e) {
        log.error("Условия не выполнены. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 500));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ErrorResponse> duplicatedDataException(final DuplicatedDataException e) {
        log.error("Добавление данных. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 409));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> notFoundException(final NotFoundException e) {
        log.error("Ресурс не найден. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 404));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> validationException(final ValidationException e) {
        log.error("Неверный запрос. " + e.getMessage());
        return Mono.just(new ErrorResponse(e.getMessage(), 400));
    }
}