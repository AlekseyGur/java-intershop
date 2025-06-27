package ru.yandex.practicum.payment;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
public class PaymentControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    BalanceRepository balanceRepository;

    @Test
    void getBalance_allParamsAreSet_shouldReturnBalance() {
        Balance testBalance = new Balance(2000);

        Mockito.when(balanceRepository.getCurrentBalance())
                .thenReturn(Mono.just(new Balance(2000)));

        webTestClient.get()
                .uri("/payments/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo(testBalance);
    }

    @Test
    void makePayment_sufficientFunds_shouldProcessPayment() {
        int paymentAmount = 500;
        Balance updatedBalance = new Balance(1500);

        Balance testBalance = new Balance(2000);

        Mockito.when(balanceRepository.getCurrentBalance())
                .thenReturn(Mono.just(testBalance));

        Mockito.when(balanceRepository.updateBalance(testBalance.getAmount() - paymentAmount))
                .thenReturn(Mono.just(updatedBalance));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments/pay")
                        .queryParam("amount", paymentAmount)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo(updatedBalance);
    }

    @Test
    void makePayment_insufficientFunds_shouldReturnBadRequest() {
        Balance testBalance = new Balance(2000);
        int paymentAmount = 3000;

        Mockito.when(balanceRepository.getCurrentBalance())
                .thenReturn(Mono.just(testBalance));

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/payments/pay")
                        .queryParam("amount", paymentAmount)
                        .build())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
