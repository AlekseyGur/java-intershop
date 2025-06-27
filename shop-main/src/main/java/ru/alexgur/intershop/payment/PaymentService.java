package ru.alexgur.intershop.payment;

import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;

public interface PaymentService {

    Mono<Balance> getCurrentBalance();

    Mono<Balance> makePayment(int amount);

}