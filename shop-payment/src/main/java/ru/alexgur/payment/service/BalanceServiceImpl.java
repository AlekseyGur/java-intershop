package ru.alexgur.payment.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import ru.alexgur.payment.error.InsufficientFundsException;
import ru.alexgur.payment.model.Balance;
import ru.alexgur.payment.repository.BalanceRepository;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;

    @Override
    public Mono<Balance> getBalance() {
        return balanceRepository.getCurrentBalance();
    }

    @Override
    public Mono<Balance> doPayment(Double amount) {
        return balanceRepository.getCurrentBalance().flatMap(
                currentBalance -> {
                    if (currentBalance.getAmount() < amount) {
                        return Mono.error(new InsufficientFundsException(currentBalance.getAmount(), amount));
                    }
                    return balanceRepository.updateBalance(currentBalance.getAmount() - amount);
                });
    }
}
