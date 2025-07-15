package ru.alexgur.intershop.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;
import ru.alexgur.payment.service.PaymentService;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ReactiveOAuth2AuthorizedClientManager manager;

    @Value("${payment.service.url}")
    private String baseUrl;

    @Override
    public Mono<Balance> getBalance(UUID userId) {
        return retrieveToken()
                .flatMap(accessToken -> WebClient.create(baseUrl)
                        .get()
                        .uri("/payments/users/" + userId + "/balance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Balance.class));
    }

    @Override
    public Mono<Balance> doPayment(UUID userId, Double amount) {
        return retrieveToken()
                .flatMap(accessToken -> WebClient.create(baseUrl)
                        .post()
                        .uri("/payments/users/" + userId + "/pay?amount=" + amount)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Balance.class));
    }

    private Mono<String> retrieveToken() {
        return manager.authorize(OAuth2AuthorizeRequest
                .withClientRegistrationId("yandex")
                .principal("system")
                .build())
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(OAuth2AccessToken::getTokenValue);
    }
}
