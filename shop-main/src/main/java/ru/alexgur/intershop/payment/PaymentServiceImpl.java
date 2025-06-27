package ru.alexgur.intershop.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;

import java.util.Map;

@Component
public class PaymentServiceImpl implements PaymentService {

    @Value("${payment.service.url}")
    private String baseUrl;

    @Override
    public Mono<Balance> getCurrentBalance() {
        return WebClient.create(baseUrl).get().uri("/payments/balance").retrieve().bodyToMono(Balance.class);
    }

    @Override
    public Mono<Balance> makePayment(int amount) {
        return WebClient.create(baseUrl).post().uri("/payments/pay?amount=" + amount).bodyValue(Map.of()).retrieve()
                .bodyToMono(Balance.class);
    }
}
