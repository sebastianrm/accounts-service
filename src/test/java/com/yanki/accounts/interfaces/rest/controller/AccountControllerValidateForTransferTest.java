package com.yanki.accounts.interfaces.rest.controller;

import com.yanki.accounts.domain.exception.BusinessException;
import com.yanki.accounts.domain.service.AccountSchedulerService;
import com.yanki.accounts.domain.service.AccountService;
import com.yanki.accounts.domain.service.AccountValidationResult;
import com.yanki.accounts.interfaces.rest.GlobalExceptionHandler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del endpoint {@code GET /api/accounts/{id}/validate-transfer}.
 * <p>
 * Este endpoint es invocado por transfers-service (vía AccountsClient) para validar
 * que una cuenta existe, está operativa y cumple los requisitos antes de ejecutar
 * una transferencia.
 * </p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@ExtendWith(MockitoExtension.class)
class AccountControllerValidateForTransferTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AccountSchedulerService accountSchedulerService;

    @InjectMocks
    private AccountController accountController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        webTestClient = WebTestClient.bindToController(accountController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        RxJavaPlugins.reset();
    }

    // ==================== Happy Path: Cuenta válida ====================

    @Nested
    @DisplayName("Validación de cuenta OPEN para transferencia")
    class ValidateOpenAccountTests {

        @Test
        @DisplayName("Debe retornar 200 con valid=true para cuenta SAVINGS OPEN")
        void validateForTransfer_savingsOpen_returns200() {
            // Given
            AccountValidationResult result = AccountValidationResult.builder()
                    .valid(true)
                    .accountId("ACC001")
                    .customerId("CUST001")
                    .status("OPEN")
                    .balance(new BigDecimal("3000.00"))
                    .productType("SAVINGS")
                    .maxFreeMovements(10)
                    .commissionPerExcessMovement(new BigDecimal("2.50"))
                    .hasOverdueDebt(false)
                    .build();

            when(accountService.validateForTransfer("ACC001")).thenReturn(Single.just(result));

            // When & Then
            webTestClient.get().uri("/api/accounts/{id}/validate-transfer", "ACC001")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.valid").isEqualTo(true)
                    .jsonPath("$.accountId").isEqualTo("ACC001")
                    .jsonPath("$.customerId").isEqualTo("CUST001")
                    .jsonPath("$.status").isEqualTo("OPEN")
                    .jsonPath("$.balance").isEqualTo(3000.00)
                    .jsonPath("$.productType").isEqualTo("SAVINGS")
                    .jsonPath("$.maxFreeMovements").isEqualTo(10)
                    .jsonPath("$.commissionPerExcessMovement").isEqualTo(2.50)
                    .jsonPath("$.hasOverdueDebt").isEqualTo(false);
        }

        @Test
        @DisplayName("Debe retornar 200 con valid=true para cuenta CURRENT OPEN")
        void validateForTransfer_currentOpen_returns200() {
            // Given
            AccountValidationResult result = AccountValidationResult.builder()
                    .valid(true)
                    .accountId("ACC002")
                    .customerId("CUST002")
                    .status("OPEN")
                    .balance(new BigDecimal("5000.00"))
                    .productType("CURRENT")
                    .maxFreeMovements(null)
                    .commissionPerExcessMovement(BigDecimal.ZERO)
                    .hasOverdueDebt(false)
                    .build();

            when(accountService.validateForTransfer("ACC002")).thenReturn(Single.just(result));

            // When & Then
            webTestClient.get().uri("/api/accounts/{id}/validate-transfer", "ACC002")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.valid").isEqualTo(true)
                    .jsonPath("$.accountId").isEqualTo("ACC002")
                    .jsonPath("$.status").isEqualTo("OPEN")
                    .jsonPath("$.productType").isEqualTo("CURRENT")
                    .jsonPath("$.balance").isEqualTo(5000.00);
        }
    }

    // ==================== Cuenta no operativa ====================

    @Nested
    @DisplayName("Validación de cuenta no operativa")
    class ValidateNonOperationalAccountTests {

        @Test
        @DisplayName("Debe retornar 200 con valid=false para cuenta CLOSED")
        void validateForTransfer_closedAccount_returns200WithInvalid() {
            // Given
            AccountValidationResult result = AccountValidationResult.builder()
                    .valid(false)
                    .accountId("ACC001")
                    .customerId("CUST001")
                    .status("CLOSED")
                    .balance(BigDecimal.ZERO)
                    .productType("SAVINGS")
                    .maxFreeMovements(10)
                    .commissionPerExcessMovement(new BigDecimal("2.50"))
                    .hasOverdueDebt(false)
                    .build();

            when(accountService.validateForTransfer("ACC001")).thenReturn(Single.just(result));

            // When & Then
            webTestClient.get().uri("/api/accounts/{id}/validate-transfer", "ACC001")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.valid").isEqualTo(false)
                    .jsonPath("$.status").isEqualTo("CLOSED");
        }

        @Test
        @DisplayName("Debe retornar 200 con valid=false para cuenta BLOCKED")
        void validateForTransfer_blockedAccount_returns200WithInvalid() {
            // Given
            AccountValidationResult result = AccountValidationResult.builder()
                    .valid(false)
                    .accountId("ACC001")
                    .customerId("CUST001")
                    .status("BLOCKED")
                    .balance(new BigDecimal("1000.00"))
                    .productType("SAVINGS")
                    .maxFreeMovements(10)
                    .commissionPerExcessMovement(new BigDecimal("2.50"))
                    .hasOverdueDebt(false)
                    .build();

            when(accountService.validateForTransfer("ACC001")).thenReturn(Single.just(result));

            // When & Then
            webTestClient.get().uri("/api/accounts/{id}/validate-transfer", "ACC001")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.valid").isEqualTo(false)
                    .jsonPath("$.status").isEqualTo("BLOCKED");
        }
    }

    // ==================== Cuenta no encontrada ====================

    @Nested
    @DisplayName("Validación de cuenta no encontrada")
    class ValidateAccountNotFoundTests {

        @Test
        @DisplayName("Debe retornar 404 si la cuenta no existe")
        void validateForTransfer_accountNotFound_returns404() {
            // Given
            when(accountService.validateForTransfer("UNKNOWN"))
                    .thenReturn(Single.error(
                            new BusinessException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada: UNKNOWN")));

            // When & Then
            webTestClient.get().uri("/api/accounts/{id}/validate-transfer", "UNKNOWN")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.code").isEqualTo("ACCOUNT_NOT_FOUND");
        }
    }

    // ==================== Cliente con deuda vencida ====================

    @Nested
    @DisplayName("Validación con deuda vencida del cliente")
    class ValidateAccountWithOverdueDebtTests {

        @Test
        @DisplayName("Debe retornar 200 con valid=true y hasOverdueDebt=true si el cliente tiene deuda vencida")
        void validateForTransfer_openAccount_withOverdueDebt_returnsValidWithDebtFlag() {
            // Given
            AccountValidationResult result = AccountValidationResult.builder()
                    .valid(true)
                    .accountId("ACC001")
                    .customerId("CUST001")
                    .status("OPEN")
                    .balance(new BigDecimal("3000.00"))
                    .productType("SAVINGS")
                    .maxFreeMovements(10)
                    .commissionPerExcessMovement(new BigDecimal("2.50"))
                    .hasOverdueDebt(true)
                    .build();

            when(accountService.validateForTransfer("ACC001")).thenReturn(Single.just(result));

            // When & Then
            webTestClient.get().uri("/api/accounts/{id}/validate-transfer", "ACC001")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.valid").isEqualTo(true)
                    .jsonPath("$.status").isEqualTo("OPEN")
                    .jsonPath("$.hasOverdueDebt").isEqualTo(true);
        }
    }
}
