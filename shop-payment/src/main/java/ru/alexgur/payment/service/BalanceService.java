package ru.alexgur.payment.service;

import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;

public interface BalanceService {

    Mono<Balance> getBalance();

    Mono<Balance> doPayment(Double amount);

}