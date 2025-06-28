package ru.alexgur.intershop.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import ru.alexgur.intershop.system.exception.PaymentException;
import ru.alexgur.payment.model.Balance;
import ru.alexgur.payment.service.PaymentService;

import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${payment.service.url}")
    private String baseUrl;

    @Override
    public Mono<Balance> getBalance() {
        return WebClient.create(baseUrl)
                .get()
                .uri("/payments/balance")
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    return Mono.error(new PaymentException("Ошибка получения баланса: " + response.statusCode()));
                })
                .bodyToMono(Balance.class);
    }

    @Override
    public Mono<Balance> doPayment(Double amount) {
        return WebClient.create(baseUrl)
                .post()
                .uri(uriBuilder -> uriBuilder.path("/payments/pay").queryParam("amount", amount).build())
                .bodyValue(Map.of())
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    return Mono.error(new PaymentException("Ошибка выполнения платежа: " + response.statusCode()));
                })
                .bodyToMono(Balance.class);
    }
}
