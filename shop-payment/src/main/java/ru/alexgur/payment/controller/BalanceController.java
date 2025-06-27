package ru.yandex.practicum.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.error.InsufficientFundsException;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Tag(name = "Сервис Платежей", description = "Управление платежами и балансом")
public class PaymentController {

    private final BalanceRepository balanceRepository;

    @GetMapping("/balance")
    @Operation(summary = "Получить текущий баланс")
    @ApiResponse(responseCode = "200", description = "Успешное получение баланса")
    public Mono<Balance> getBalance() {
        return balanceRepository.getCurrentBalance();
    }

    @PostMapping("/pay")
    @Operation(summary = "Осуществить платеж")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платеж успешно обработан"),
            @ApiResponse(responseCode = "400", description = "Недостаточно средств")
    })
    public Mono<Balance> makePayment(@Parameter(description = "Сумма платежа") @RequestParam int amount) {
        return balanceRepository.getCurrentBalance()
                .flatMap(currentBalance -> {
                    if (currentBalance.getAmount() < amount) {
                        return Mono.error(new InsufficientFundsException(currentBalance.getAmount(), amount));
                    }
                    return balanceRepository.updateBalance(currentBalance.getAmount() - amount);
                });
    }
}
