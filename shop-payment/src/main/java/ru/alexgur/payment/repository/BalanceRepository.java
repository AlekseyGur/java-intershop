package ru.yandex.practicum.payment;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BalanceRepository {

    private static final Balance balance = new Balance(10000);

    public Mono<Balance> getCurrentBalance() {
        return Mono.just(balance);
    }

    public Mono<Balance> updateBalance(int newAmount) {
        balance.setAmount(newAmount);
        return Mono.just(balance);
    }
}
