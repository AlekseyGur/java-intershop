package ru.alexgur.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.alexgur.payment.model.Balance;
import ru.alexgur.payment.service.BalanceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "Сервис платежей", description = "Совершение платежей и получение баланса")
@Validated
public class PaymentController {

    private final BalanceService balanceService;

    @GetMapping("/balance")
    @Operation(summary = "Получить текущий баланс")
    @ApiResponse(responseCode = "200", description = "Успешное получение баланса")
    public Mono<Balance> getBalance() {
        return balanceService.getBalance();
    }

    @PostMapping("/pay")
    @Operation(summary = "Сделать платеж")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платеж успешно обработан"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств")
    })
    public Mono<Balance> makePayment(@Parameter(description = "Сумма платежа") @RequestParam @Positive Double amount) {
        return balanceService.doPayment(amount);
    }
}
