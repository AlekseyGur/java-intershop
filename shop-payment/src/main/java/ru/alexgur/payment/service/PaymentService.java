package ru.alexgur.payment.service;

import java.util.UUID;

import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;

public interface PaymentService {

    Mono<Balance> getBalance(UUID userId);

    Mono<Balance> doPayment(UUID userId, Double amount);

}