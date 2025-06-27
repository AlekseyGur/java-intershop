package ru.yandex.practicum.error;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(double currentBalance, double requiredAmount) {
        super(String.format("Insufficient funds. Current: %.2f, Required: %.2f",
                currentBalance, requiredAmount));
    }
}
