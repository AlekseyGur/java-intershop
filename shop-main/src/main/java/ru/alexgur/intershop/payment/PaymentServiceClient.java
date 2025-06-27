package ru.alexgur.intershop.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class PaymentServiceClient {

    @Value("${payment.service.url}")
    private String baseUrl;

    public Mono<Balance> getCurrentBalance() {
        return WebClient.create(baseUrl)
                .get()
                .uri("/payments/balance")
                .retrieve()
                .bodyToMono(Balance.class);
    }

    public Mono<Balance> makePayment(int amount) {
        return WebClient.create(baseUrl)
                .post()
                .uri("/payments/pay?amount=" + amount)
                .bodyValue(Map.of())
                .retrieve()
                .bodyToMono(Balance.class);
    }
}
