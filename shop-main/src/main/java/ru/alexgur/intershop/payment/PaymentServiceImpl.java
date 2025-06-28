package ru.alexgur.intershop.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;
import ru.alexgur.payment.service.PaymentService;

import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${payment.service.url}")
    private String baseUrl;

    @Override
    public Mono<Balance> getBalance() {
        return WebClient.create(baseUrl).get().uri("/payments/balance").retrieve().bodyToMono(Balance.class);
    }

    @Override
    public Mono<Balance> doPayment(Double amount) {
        return WebClient.create(baseUrl).post().uri("/payments/pay?amount=" + amount).bodyValue(Map.of()).retrieve()
                .bodyToMono(Balance.class);
    }
}
