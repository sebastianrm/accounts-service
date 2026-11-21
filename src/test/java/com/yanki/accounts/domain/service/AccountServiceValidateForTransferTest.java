package com.yanki.accounts.domain.service;

import com.yanki.accounts.domain.exception.BusinessException;
import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.CustomerRiskView;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.port.out.CustomerRiskViewPort;
import com.yanki.accounts.interfaces.rest.dto.AccountValidationResponse;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias para la comunicación inter-servicio entre transfers-service y accounts-service.
 * <p>
 * Cubre el método {@link AccountService#validateForTransfer(String)} que es invocado por
 * transfers-service vía {@code GET /api/accounts/{id}/validate-transfer} para validar
 * cuentas origen y destino antes de ejecutar una transferencia.
 * </p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceValidateForTransferTest {

    @Mock
    private AccountPersistencePort accountPersistencePort;

    @Mock
    private CustomerRiskViewPort riskViewPort;

    private AccountService accountService;

    private Account createTestAccount(String id, AccountStatus status) {
        return Account.builder()
                .id(id)
                .customerId("CUST001")
                .accountNumber("ACC123456789")
                .type(AccountType.SAVINGS)
                .status(status)
                .currency("PEN")
                .balance(new BigDecimal("3000.00"))
                .openedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .monthlyMovementCount(0)
                .currentMonth("2026-11")
                .maxFreeMovementsPerMonth(10)
                .commissionPerExcessMovement(new BigDecimal("2.50"))
                .maintenanceCommission(BigDecimal.ZERO)
                .build();
    }

    private CustomerRiskView createCleanRiskView() {
        return CustomerRiskView.builder()
                .customerId("CUST001")
                .hasOverdueDebt(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CustomerRiskView createOverdueRiskView() {
        return CustomerRiskView.builder()
                .customerId("CUST001")
                .hasOverdueDebt(true)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        accountService = new AccountService(
                accountPersistencePort,
                null, // movementPersistencePort - no needed for validateForTransfer
                null, // profileViewPort - no needed for validateForTransfer
                null, // cardViewPort - no needed for validateForTransfer
                riskViewPort,
                null, // idempotencyPort - no needed for validateForTransfer
                null, // eventPublisherPort - no needed for validateForTransfer
                null  // cachePort - no needed for validateForTransfer
        );
    }

    @AfterEach
    void tearDown() {
        RxJavaPlugins.reset();
    }

    // ==================== Happy Path: Cuenta válida para transferencia ====================

    @Nested
    @DisplayName("Validación de cuenta OPEN para transferencia")
    class ValidateOpenAccountTests {

        @Test
        @DisplayName("Debe retornar valid=true para cuenta SAVINGS OPEN sin deuda vencida")
        void validateForTransfer_savingsOpen_noDebt_success() {
            // Given
            Account account = createTestAccount("ACC001", AccountStatus.OPEN);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("ACC001").test();

            // Then
            observer.assertComplete();
            observer.assertNoErrors();
            AccountValidationResult result = observer.values().get(0);

            assertTrue(result.isValid());
            assertEquals("ACC001", result.getAccountId());
            assertEquals("CUST001", result.getCustomerId());
            assertEquals("OPEN", result.getStatus());
            assertEquals(new BigDecimal("3000.00"), result.getBalance());
            assertEquals("SAVINGS", result.getProductType());
            assertEquals(10, result.getMaxFreeMovements());
            assertEquals(new BigDecimal("2.50"), result.getCommissionPerExcessMovement());
            assertFalse(result.isHasOverdueDebt());
        }

        @Test
        @DisplayName("Debe retornar valid=true para cuenta CURRENT OPEN sin deuda vencida")
        void validateForTransfer_currentOpen_noDebt_success() {
            // Given
            Account account = Account.builder()
                    .id("ACC002")
                    .customerId("CUST002")
                    .accountNumber("ACC987654321")
                    .type(AccountType.CURRENT)
                    .status(AccountStatus.OPEN)
                    .currency("PEN")
                    .balance(new BigDecimal("5000.00"))
                    .openedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .monthlyMovementCount(0)
                    .currentMonth("2026-11")
                    .maxFreeMovementsPerMonth(null)
                    .commissionPerExcessMovement(BigDecimal.ZERO)
                    .maintenanceCommission(new BigDecimal("15.00"))
                    .build();

            when(accountPersistencePort.findById("ACC002"))
                    .thenReturn(Maybe.just(account));
            when(riskViewPort.findByCustomerId("CUST002"))
                    .thenReturn(Maybe.just(CustomerRiskView.builder()
                    .customerId("CUST002")
                    .hasOverdueDebt(false)
                    .updatedAt(LocalDateTime.now())
                    .build()));

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("ACC002").test();

            // Then
            observer.assertComplete();
            observer.assertNoErrors();
            AccountValidationResult result = observer.values().get(0);

            assertTrue(result.isValid());
            assertEquals("ACC002", result.getAccountId());
            assertEquals("CUST002", result.getCustomerId());
            assertEquals("OPEN", result.getStatus());
            assertEquals(new BigDecimal("5000.00"), result.getBalance());
            assertEquals("CURRENT", result.getProductType());
            assertFalse(result.isHasOverdueDebt());
        }
    }

    // ==================== Cuenta no operativa ====================

    @Nested
    @DisplayName("Validación de cuenta no operativa para transferencia")
    class ValidateNonOperationalAccountTests {

        @Test
        @DisplayName("Debe retornar valid=false para cuenta CLOSED")
        void validateForTransfer_closedAccount_returnsInvalid() {
            // Given
            Account account = createTestAccount("ACC001", AccountStatus.CLOSED);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("ACC001").test();

            // Then
            observer.assertComplete();
            observer.assertNoErrors();
            AccountValidationResult result = observer.values().get(0);

            assertFalse(result.isValid());
            assertEquals("ACC001", result.getAccountId());
            assertEquals("CLOSED", result.getStatus());
        }

        @Test
        @DisplayName("Debe retornar valid=false para cuenta BLOCKED")
        void validateForTransfer_blockedAccount_returnsInvalid() {
            // Given
            Account account = createTestAccount("ACC001", AccountStatus.BLOCKED);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("ACC001").test();

            // Then
            observer.assertComplete();
            observer.assertNoErrors();
            AccountValidationResult result = observer.values().get(0);

            assertFalse(result.isValid());
            assertEquals("ACC001", result.getAccountId());
            assertEquals("BLOCKED", result.getStatus());
        }
    }

    // ==================== Cuenta no encontrada ====================

    @Nested
    @DisplayName("Validación de cuenta no encontrada")
    class ValidateAccountNotFoundTests {

        @Test
        @DisplayName("Debe lanzar BusinessException ACCOUNT_NOT_FOUND si la cuenta no existe")
        void validateForTransfer_accountNotFound_throwsError() {
            // Given
            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("UNKNOWN").test();

            // Then
            observer.assertError(BusinessException.class);
            observer.assertError(error -> {
                assertTrue(error instanceof BusinessException);
                BusinessException be = (BusinessException) error;
                assertEquals("ACCOUNT_NOT_FOUND", be.getCode());
                return true;
            });
        }
    }

    // ==================== Cliente con deuda vencida ====================

    @Nested
    @DisplayName("Validación con deuda vencida del cliente")
    class ValidateAccountWithOverdueDebtTests {

        @Test
        @DisplayName("Debe retornar valid=true con hasOverdueDebt=true si el cliente tiene deuda vencida")
        void validateForTransfer_openAccount_withOverdueDebt_returnsValidWithDebtFlag() {
            // Given
            Account account = createTestAccount("ACC001", AccountStatus.OPEN);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createOverdueRiskView()));

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("ACC001").test();

            // Then
            observer.assertComplete();
            observer.assertNoErrors();
            AccountValidationResult result = observer.values().get(0);

            // La cuenta sigue siendo válida (OPEN), pero se marca la deuda vencida
            // para que transfers-service decida si bloquear la transferencia.
            assertTrue(result.isValid());
            assertEquals("ACC001", result.getAccountId());
            assertEquals("OPEN", result.getStatus());
            assertTrue(result.isHasOverdueDebt());
        }

        @Test
        @DisplayName("Debe retornar hasOverdueDebt=false si no hay risk view (cliente limpio)")
        void validateForTransfer_openAccount_noRiskView_returnsNoDebt() {
            // Given
            Account account = createTestAccount("ACC001", AccountStatus.OPEN);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.empty());

            // When
            TestObserver<AccountValidationResult> observer = accountService.validateForTransfer("ACC001").test();

            // Then
            observer.assertComplete();
            observer.assertNoErrors();
            AccountValidationResult result = observer.values().get(0);

            assertTrue(result.isValid());
            assertFalse(result.isHasOverdueDebt());
        }
    }

    // ==================== Mapeo correcto del DTO de respuesta ====================

    @Nested
    @DisplayName("Mapeo correcto del DTO AccountValidationResponse")
    class AccountValidationResponseMappingTests {

        @Test
        @DisplayName("AccountValidationResponse debe mapear correctamente desde AccountValidationResult")
        void accountValidationResponse_fromResult_correctMapping() {
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

            // When
            AccountValidationResponse response = AccountValidationResponse.builder()
                    .valid(result.isValid())
                    .accountId(result.getAccountId())
                    .customerId(result.getCustomerId())
                    .status(result.getStatus())
                    .balance(result.getBalance())
                    .productType(result.getProductType())
                    .maxFreeMovements(result.getMaxFreeMovements())
                    .commissionPerExcessMovement(result.getCommissionPerExcessMovement())
                    .hasOverdueDebt(result.isHasOverdueDebt())
                    .build();

            // Then
            assertTrue(response.isValid());
            assertEquals("ACC001", response.getAccountId());
            assertEquals("CUST001", response.getCustomerId());
            assertEquals("OPEN", response.getStatus());
            assertEquals(new BigDecimal("3000.00"), response.getBalance());
            assertEquals("SAVINGS", response.getProductType());
            assertEquals(10, response.getMaxFreeMovements());
            assertEquals(new BigDecimal("2.50"), response.getCommissionPerExcessMovement());
            assertFalse(response.isHasOverdueDebt());
        }
    }
}
