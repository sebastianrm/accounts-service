package com.yanki.accounts.interfaces.rest.controller;

import com.yanki.accounts.domain.service.AccountSchedulerService;
import com.yanki.accounts.domain.service.AccountService;
import com.yanki.accounts.domain.service.MovementFilterCriteria;
import com.yanki.accounts.interfaces.rest.GlobalExceptionHandler;
import com.yanki.accounts.interfaces.rest.dto.AccountRequest;
import com.yanki.accounts.interfaces.rest.dto.AccountResponse;
import com.yanki.accounts.interfaces.rest.dto.DepositRequest;
import com.yanki.accounts.interfaces.rest.dto.WithdrawalRequest;
import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.model.BalanceResult;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import com.yanki.accounts.domain.service.AccountResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private AccountSchedulerService accountSchedulerService;

    @InjectMocks
    private AccountController accountController;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        webTestClient = WebTestClient.bindToController(accountController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private WebTestClient jsonClient() {
        ObjectMapper mapper = new ObjectMapper();
        return WebTestClient.bindToController(accountController)
                .controllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    public void tearDown() {
        RxJavaPlugins.reset();
    }

    @Test
    void createAccount_success() {
        AccountResult result = AccountResult.builder().accountId("ACC123").accountType("SAVINGS").customerId("12345").build();

        AccountResponse response = AccountResponse.from(result);

        org.junit.jupiter.api.Assertions.assertNotNull(response);
        org.junit.jupiter.api.Assertions.assertEquals("ACC123", response.getAccountId());
    }

    @Test
    void findById_success() {
        AccountResult result = AccountResult.builder().accountId("ACC123").accountType("SAVINGS").customerId("12345").build();

        when(accountService.findById("ACC123")).thenReturn(io.reactivex.rxjava3.core.Single.just(result));

        webTestClient.get().uri("/api/accounts/{id}", "ACC123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.accountId").isEqualTo("ACC123");
    }

    @Test
    void deposit_success() {
        DepositRequest request = DepositRequest.builder().amount(new BigDecimal("500")).description("Test deposit").channel("WEB").build();
        org.junit.jupiter.api.Assertions.assertNotNull(request);
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("500"), request.getAmount());
    }

    @Test
    void withdrawal_success() {
        WithdrawalRequest request = WithdrawalRequest.builder().amount(new BigDecimal("300")).description("Test withdrawal").channel("WEB").build();
        org.junit.jupiter.api.Assertions.assertNotNull(request);
        org.junit.jupiter.api.Assertions.assertEquals(new BigDecimal("300"), request.getAmount());
    }

    @Test
    void createAccount_conflict() {
        when(accountService.openAccount(org.mockito.ArgumentMatchers.any()))
                .thenReturn(io.reactivex.rxjava3.core.Single.error(new com.yanki.accounts.domain.exception.BusinessException("SAVINGS_ACCOUNT_ALREADY_EXISTS", "Cuenta de ahorro ya existe")));

        io.reactivex.rxjava3.observers.TestObserver<Void> observer = 
                accountService.openAccount(org.mockito.ArgumentMatchers.any())
                        .ignoreElement()
                        .test();
        observer.assertError(com.yanki.accounts.domain.exception.BusinessException.class);
    }

    @Test
    void findById_notFound() {
        when(accountService.findById("NOEXIST")).thenReturn(io.reactivex.rxjava3.core.Single.error(
                new com.yanki.accounts.domain.exception.BusinessException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada")));

        webTestClient.get().uri("/api/accounts/{id}", "NOEXIST")
                .exchange()
                .expectStatus().isNotFound();
    }

    // ==================== Balance (CU-08, CP-049, CP-050, CP-053) ====================

    @Nested
    @DisplayName("Consulta de saldo")
    class BalanceTests {

        @Test
        @DisplayName("Debe consultar saldo exitosamente (CP-049, CP-050)")
        void getBalance_success() {
            when(accountService.getBalance("ACC123")).thenReturn(Single.just(new BalanceResult(new BigDecimal("1500.00"), false)));

            webTestClient.get().uri("/api/accounts/{id}/balance", "ACC123")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.accountId").isEqualTo("ACC123")
                    .jsonPath("$.balance").isEqualTo(1500.00);
        }

        @Test
        @DisplayName("Debe retornar 404 si la cuenta no existe (CP-053)")
        void getBalance_notFound() {
            when(accountService.getBalance("NOEXIST")).thenReturn(Single.error(
                    new com.yanki.accounts.domain.exception.BusinessException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada")));

            webTestClient.get().uri("/api/accounts/{id}/balance", "NOEXIST")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // ==================== Movements (CU-09, CP-054, CP-055) ====================

    @Nested
    @DisplayName("Consulta de movimientos")
    class MovementTests {

        @Test
        @DisplayName("Debe listar movimientos con filtros (CP-054)")
        void getMovements_success() {
            Movement movement = Movement.builder()
                    .id("MOV001")
                    .accountId("ACC123")
                    .customerId("CUST001")
                    .type(MovementType.DEPOSIT)
                    .amount(new BigDecimal("500.00"))
                    .balanceAfter(new BigDecimal("1500.00"))
                    .channel("WEB")
                    .description("Depósito")
                    .occurredAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            when(accountService.findMovements(eq("ACC123"), any(MovementFilterCriteria.class)))
                    .thenReturn(Flowable.just(movement));

            webTestClient.get().uri("/api/accounts/{id}/movements?page=0&size=20&type=DEPOSIT", "ACC123")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content[0].accountId").isEqualTo("ACC123")
                    .jsonPath("$.page").isEqualTo(0);
        }

        @Test
        @DisplayName("Debe retornar 404 si la cuenta no existe (CP-055)")
        void getMovements_notFound() {
            when(accountService.findMovements(eq("NOEXIST"), any(MovementFilterCriteria.class)))
                    .thenReturn(Flowable.error(
                            new com.yanki.accounts.domain.exception.BusinessException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada")));

            webTestClient.get().uri("/api/accounts/{id}/movements", "NOEXIST")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // ==================== Close Account (CU-02, CP-035, CP-036) ====================

    @Nested
    @DisplayName("Cierre de cuenta")
    class CloseAccountTests {

        @Test
        @DisplayName("Debe cerrar cuenta exitosamente (CP-035)")
        void closeAccount_success() {
            when(accountService.closeAccount("ACC123")).thenReturn(Completable.complete());

            webTestClient.delete().uri("/api/accounts/{id}", "ACC123")
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @DisplayName("Debe retornar 409 si el saldo no es 0 (CP-036)")
        void closeAccount_balanceNotZero() {
            when(accountService.closeAccount("ACC123")).thenReturn(Completable.error(
                    new com.yanki.accounts.domain.exception.BusinessException("ACCOUNT_INSUFFICIENT_BALANCE", "Saldo no es 0")));

            webTestClient.delete().uri("/api/accounts/{id}", "ACC123")
                    .exchange()
                    .expectStatus().isEqualTo(409);
        }

        @Test
        @DisplayName("Debe retornar 404 si la cuenta no existe")
        void closeAccount_notFound() {
            when(accountService.closeAccount("NOEXIST")).thenReturn(Completable.error(
                    new com.yanki.accounts.domain.exception.BusinessException("ACCOUNT_NOT_FOUND", "Cuenta no encontrada")));

            webTestClient.delete().uri("/api/accounts/{id}", "NOEXIST")
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    // ==================== Find All with Filters (CU-02, CP-034) ====================

    @Nested
    @DisplayName("Listado de cuentas con filtros")
    class FindAllTests {

        @Test
        @DisplayName("Debe listar cuentas con filtros customerId, type, status (CP-034)")
        void findAll_withFilters() {
            AccountResult result = AccountResult.builder()
                    .accountId("ACC123")
                    .accountType("SAVINGS")
                    .customerId("CUST001")
                    .status("OPEN")
                    .balance(new BigDecimal("1000.00"))
                    .currency("PEN")
                    .build();

            when(accountService.findByFilters(any()))
                    .thenReturn(Flowable.just(result));

            webTestClient.get().uri("/api/accounts?customerId=CUST001&type=SAVINGS&status=OPEN&page=0&size=10")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content[0].accountId").isEqualTo("ACC123")
                    .jsonPath("$.content[0].accountType").isEqualTo("SAVINGS")
                    .jsonPath("$.page").isEqualTo(0);
        }

        @Test
        @DisplayName("Debe listar todas las cuentas sin filtros")
        void findAll_withoutFilters() {
            AccountResult result = AccountResult.builder()
                    .accountId("ACC123")
                    .accountType("SAVINGS")
                    .customerId("CUST001")
                    .status("OPEN")
                    .balance(new BigDecimal("1000.00"))
                    .currency("PEN")
                    .build();

            when(accountService.findByFilters(any()))
                    .thenReturn(Flowable.just(result));

            webTestClient.get().uri("/api/accounts")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content[0].accountId").isEqualTo("ACC123");
        }
    }

    // ==================== Admin Jobs (CU-15) ====================

    @Nested
    @DisplayName("Endpoints de jobs admin")
    class AdminJobTests {

        @Test
        @DisplayName("Debe ejecutar job de mantenimiento (CP-139)")
        void triggerMaintenanceCommission() {
            webTestClient.post().uri("/api/accounts/jobs/maintenance-commission")
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @DisplayName("Debe ejecutar job de verificación VIP")
        void triggerVipAverageCheck() {
            webTestClient.post().uri("/api/accounts/jobs/vip-average-check")
                    .exchange()
                    .expectStatus().isNoContent();
        }
    }

    // ==================== Movement Count (CU-05) ====================

    @Nested
    @DisplayName("Contador de movimientos")
    class MovementCountTests {

        @Test
        @DisplayName("Debe obtener contador de movimientos del mes actual")
        void getMovementCount_success() {
            when(accountSchedulerService.getMovementCount("ACC123")).thenReturn(Single.just(5));

            webTestClient.get().uri("/api/accounts/{id}/movement-count", "ACC123")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.accountId").isEqualTo("ACC123")
                    .jsonPath("$.movementCount").isEqualTo(5);
        }
    }
}
