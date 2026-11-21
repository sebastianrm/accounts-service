package com.yanki.accounts.domain.service;

import com.yanki.accounts.domain.exception.BusinessException;
import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.BalanceResult;
import com.yanki.accounts.domain.model.CustomerCardView;
import com.yanki.accounts.domain.model.CustomerProfileView;
import com.yanki.accounts.domain.model.CustomerRiskView;
import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.port.out.CachePort;
import com.yanki.accounts.domain.port.out.CustomerCardViewPort;
import com.yanki.accounts.domain.port.out.CustomerProfileViewPort;
import com.yanki.accounts.domain.port.out.CustomerRiskViewPort;
import com.yanki.accounts.domain.port.out.EventPublisherPort;
import com.yanki.accounts.domain.port.out.IdempotencyPort;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.yanki.accounts.domain.model.CustomerCardView;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountPersistencePort accountPersistencePort;

    @Mock
    private MovementPersistencePort movementPersistencePort;

    @Mock
    private CustomerProfileViewPort profileViewPort;

    @Mock
    private CustomerCardViewPort cardViewPort;

    @Mock
    private CustomerRiskViewPort riskViewPort;

    @Mock
    private IdempotencyPort idempotencyPort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private CachePort cachePort;

    private AccountService accountService;

    private Account createTestAccount(String id) {
        return Account.builder()
                .id(id)
                .customerId("CUST001")
                .accountNumber("ACC123456789")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.OPEN)
                .currency("PEN")
                .balance(new BigDecimal("1000.00"))
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

    private CustomerProfileView createPersonalProfile() {
        return CustomerProfileView.builder()
                .customerId("CUST001")
                .customerType("PERSONAL")
                .profile("STANDARD")
                .status("ACTIVE")
                .documentType("DNI")
                .documentNumber("12345678")
                .email("test@yanki.com")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private CustomerRiskView createCleanRiskView() {
        return CustomerRiskView.builder()
                .customerId("CUST001")
                .hasOverdueDebt(false)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        accountService = new AccountService(
                accountPersistencePort,
                movementPersistencePort,
                profileViewPort,
                cardViewPort,
                riskViewPort,
                idempotencyPort,
                eventPublisherPort,
                cachePort
        );
    }

    @AfterEach
    void tearDown() {
        RxJavaPlugins.reset();
    }

    // ==================== Open Account ====================

    @Nested
    @DisplayName("Abrir cuenta")
    class OpenAccountTests {

        @Test
        @DisplayName("Debe abrir cuenta de ahorro para cliente PERSONAL STANDARD")
        void openSavingsAccount_success() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("500.00"))
                    .channel("WEB")
                    .build();

            CustomerProfileView profile = createPersonalProfile();
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(profile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(accountPersistencePort.existsByCustomerIdAndType("CUST001", AccountType.SAVINGS))
                    .thenReturn(Single.just(false));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> {
                        Account a = inv.getArgument(0);
                        a.setId("ACC_NEW");
                        return Single.just(a);
                    });
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_OPEN");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishAccountOpened(any()))
                    .thenReturn(Completable.complete());

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            AccountResult result = observer.values().get(0);
            assertEquals("ACC_NEW", result.getAccountId());
            assertEquals("SAVINGS", result.getAccountType());
            verify(eventPublisherPort).publishAccountOpened(any());
        }

        @Test
        @DisplayName("Debe rechazar apertura si el cliente no existe")
        void openAccount_customerNotFound() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("UNKNOWN")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("100.00"))
                    .build();

            when(profileViewPort.findByCustomerId("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }

        @Test
        @DisplayName("Debe rechazar apertura si el cliente tiene deuda vencida")
        void openAccount_customerHasOverdueDebt() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("100.00"))
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            CustomerRiskView riskView = CustomerRiskView.builder()
                    .customerId("CUST001")
                    .hasOverdueDebt(true)
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(riskView));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }

        @Test
        @DisplayName("Debe rechazar cuenta de ahorro para cliente ENTERPRISE")
        void openSavingsAccount_enterpriseNotAllowed() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("100.00"))
                    .build();

            CustomerProfileView enterpriseProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("ENTERPRISE")
                    .profile("STANDARD")
                    .status("ACTIVE")
                    .documentType("RUC")
                    .documentNumber("20123456789")
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(enterpriseProfile));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }

        @Test
        @DisplayName("Debe rechazar si ya existe cuenta de ahorro para el cliente")
        void openAccount_savingsAlreadyExists() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("100.00"))
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(accountPersistencePort.existsByCustomerIdAndType("CUST001", AccountType.SAVINGS))
                    .thenReturn(Single.just(true));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Find Account ====================

    @Nested
    @DisplayName("Buscar cuenta")
    class FindAccountTests {

        @Test
        @DisplayName("Debe encontrar cuenta por ID")
        void findById_success() {
            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));

            TestObserver<AccountResult> observer = accountService.findById("ACC001").test();

            observer.assertComplete();
            observer.assertNoErrors();
            AccountResult result = observer.values().get(0);
            assertEquals("ACC001", result.getAccountId());
            assertEquals(new BigDecimal("1000.00"), result.getBalance());
        }

        @Test
        @DisplayName("Debe lanzar error si la cuenta no existe")
        void findById_notFound() {
            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            TestObserver<AccountResult> observer = accountService.findById("UNKNOWN").test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Get Balance ====================

    @Nested
    @DisplayName("Consultar saldo")
    class GetBalanceTests {

        @Test
        @DisplayName("Debe devolver saldo desde caché si existe")
        void getBalance_fromCache() {
            when(cachePort.getBalance("ACC001"))
                    .thenReturn(Single.just(new BigDecimal("1500.00")));

            TestObserver<BalanceResult> observer = accountService.getBalance("ACC001").test();

            observer.assertComplete();
            assertEquals(new BigDecimal("1500.00"), observer.values().get(0).getBalance());
            assertTrue(observer.values().get(0).isCached());
            verify(accountPersistencePort, never()).findById(any());
        }

        @Test
        @DisplayName("Debe cargar saldo desde DB si caché está vacía")
        void getBalance_fromDatabase() {
            when(cachePort.getBalance("ACC001"))
                    .thenReturn(Single.just(BigDecimal.ZERO));
            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            doNothing().when(cachePort).setBalance(eq("ACC001"), any());

            TestObserver<BalanceResult> observer = accountService.getBalance("ACC001").test();

            observer.assertComplete();
            assertEquals(new BigDecimal("1000.00"), observer.values().get(0).getBalance());
            assertFalse(observer.values().get(0).isCached());
        }
    }

    // ==================== Deposit ====================

    @Nested
    @DisplayName("Depósito")
    class DepositTests {

        @Test
        @DisplayName("Debe procesar depósito exitosamente")
        void deposit_success() {
            DepositCommand command = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("200.00"))
                    .description("Depósito en caja")
                    .channel("BRANCH")
                    .build();

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV001");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            MovementResult result = observer.values().get(0);
            assertEquals(new BigDecimal("1200.00"), result.getBalanceAfter());
        }

        @Test
        @DisplayName("Debe rechazar depósito si la cuenta no existe")
        void deposit_accountNotFound() {
            DepositCommand command = DepositCommand.builder()
                    .accountId("UNKNOWN")
                    .amount(new BigDecimal("200.00"))
                    .build();

            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Withdrawal ====================

    @Nested
    @DisplayName("Retiro")
    class WithdrawalTests {

        @Test
        @DisplayName("Debe procesar retiro exitosamente")
        void withdrawal_success() {
            WithdrawalCommand command = WithdrawalCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("300.00"))
                    .description("Retiro en cajero")
                    .channel("ATM")
                    .build();

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV002");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            TestObserver<MovementResult> observer = accountService.withdrawal(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            MovementResult result = observer.values().get(0);
            assertEquals(new BigDecimal("700.00"), result.getBalanceAfter());
        }

        @Test
        @DisplayName("Debe rechazar retiro por saldo insuficiente")
        void withdrawal_insufficientBalance() {
            WithdrawalCommand command = WithdrawalCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("5000.00"))
                    .build();

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));

            TestObserver<MovementResult> observer = accountService.withdrawal(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Close Account ====================

    @Nested
    @DisplayName("Cerrar cuenta")
    class CloseAccountTests {

        @Test
        @DisplayName("Debe cerrar cuenta con saldo 0")
        void closeAccount_success() {
            Account account = createTestAccount("ACC001");
            account.setBalance(BigDecimal.ZERO);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));
            doNothing().when(cachePort).invalidateBalance("ACC001");
            when(eventPublisherPort.publishAccountClosed(any()))
                    .thenReturn(Completable.complete());

            TestObserver<Void> observer = accountService.closeAccount("ACC001").test();

            observer.assertComplete();
            observer.assertNoErrors();
            assertEquals(AccountStatus.CLOSED, account.getStatus());
            assertNotNull(account.getClosedAt());
        }

        @Test
        @DisplayName("Debe rechazar cierre si el saldo no es 0")
        void closeAccount_balanceNotZero() {
            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));

            TestObserver<Void> observer = accountService.closeAccount("ACC001").test();

            observer.assertError(BusinessException.class);
        }

        @Test
        @DisplayName("Debe rechazar cierre si la cuenta no existe")
        void closeAccount_notFound() {
            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            TestObserver<Void> observer = accountService.closeAccount("UNKNOWN").test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Settle Debit Payment ====================

    @Nested
    @DisplayName("Liquidar pago débito")
    class SettleDebitPaymentTests {

        @Test
        @DisplayName("Debe liquidar pago débito exitosamente")
        void settleDebitPayment_success() {
            DebitPaymentCommand command = DebitPaymentCommand.builder()
                    .paymentId("PAY001")
                    .cardId("CARD001")
                    .customerId("CUST001")
                    .accountId("ACC001")
                    .amount(new BigDecimal("50.00"))
                    .occurredAt(LocalDateTime.now())
                    .build();

            when(idempotencyPort.checkAndMarkProcessed(any()))
                    .thenReturn(Single.just(false));

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_PAY");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            when(eventPublisherPort.publishDebitPaymentSettled(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            TestObserver<Void> observer = accountService.settleDebitPayment(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
        }

        @Test
        @DisplayName("Debe saltar procesamiento si el evento ya fue procesado (idempotencia)")
        void settleDebitPayment_alreadyProcessed() {
            DebitPaymentCommand command = DebitPaymentCommand.builder()
                    .paymentId("PAY001")
                    .cardId("CARD001")
                    .customerId("CUST001")
                    .accountId("ACC001")
                    .amount(new BigDecimal("50.00"))
                    .occurredAt(LocalDateTime.now())
                    .build();

            when(idempotencyPort.checkAndMarkProcessed(any()))
                    .thenReturn(Single.just(true));

            TestObserver<Void> observer = accountService.settleDebitPayment(command).test();

            observer.assertComplete();
            verify(accountPersistencePort, never()).findById(any());
        }
    }

    // ==================== Settle Transfer ====================

    @Nested
    @DisplayName("Liquidar transferencia")
    class SettleTransferTests {

        @Test
        @DisplayName("Debe liquidar transferencia exitosamente")
        void settleTransfer_success() {
            TransferCommand command = TransferCommand.builder()
                    .transferId("TRF001")
                    .sourceAccountId("ACC001")
                    .targetAccountId("ACC002")
                    .amount(new BigDecimal("100.00"))
                    .description("Transferencia interna")
                    .requestedBy("CUST001")
                    .occurredAt(LocalDateTime.now())
                    .build();

            when(idempotencyPort.checkAndMarkProcessed(any()))
                    .thenReturn(Single.just(false));

            Account source = createTestAccount("ACC001");
            Account target = createTestAccount("ACC002");
            target.setCustomerId("CUST002");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(source));
            when(accountPersistencePort.findById("ACC002"))
                    .thenReturn(Maybe.just(target));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_TRF");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            when(eventPublisherPort.publishTransferSettled(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance(any());

            TestObserver<Void> observer = accountService.settleTransfer(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
        }

        @Test
        @DisplayName("Debe rechazar transferencia a la misma cuenta")
        void settleTransfer_sameAccount() {
            TransferCommand command = TransferCommand.builder()
                    .transferId("TRF001")
                    .sourceAccountId("ACC001")
                    .targetAccountId("ACC001")
                    .amount(new BigDecimal("100.00"))
                    .occurredAt(LocalDateTime.now())
                    .build();

            when(idempotencyPort.checkAndMarkProcessed(any()))
                    .thenReturn(Single.just(false));

            TestObserver<Void> observer = accountService.settleTransfer(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Find Movements ====================

    @Nested
    @DisplayName("Buscar movimientos")
    class FindMovementsTests {

        @Test
        @DisplayName("Debe devolver movimientos de la cuenta")
        void findMovements_success() {
            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(movementPersistencePort.findAllByAccountIdOrderByOccurredAtDesc("ACC001"))
                    .thenReturn(Flowable.just(
                            Movement.builder().id("MOV001").accountId("ACC001").customerId("CUST001")
                                    .type(MovementType.DEPOSIT).amount(new BigDecimal("200.00"))
                                    .occurredAt(LocalDateTime.now()).build()
                    ));

            MovementFilterCriteria criteria = MovementFilterCriteria.builder().build();
            TestSubscriber<Movement> subscriber = accountService.findMovements("ACC001", criteria).test();

            subscriber.assertComplete();
            subscriber.assertValueCount(1);
        }

        @Test
        @DisplayName("Debe lanzar error si la cuenta no existe")
        void findMovements_accountNotFound() {
            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            MovementFilterCriteria criteria = MovementFilterCriteria.builder().build();
            TestSubscriber<Movement> subscriber = accountService.findMovements("UNKNOWN", criteria).test();

            subscriber.assertError(BusinessException.class);
        }
    }

    // ==================== Open Account — VIP (CU-14, CP-021, CP-131) ====================

    @Nested
    @DisplayName("Abrir cuenta VIP")
    class OpenVipAccountTests {

        @Test
        @DisplayName("Debe abrir cuenta ahorro VIP con tarjeta de crédito activa (CP-021, CP-131)")
        void openVipSavingsAccount_withCreditCard() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("5000.00"))
                    .channel("WEB")
                    .build();

            CustomerProfileView vipProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("PERSONAL")
                    .profile("VIP")
                    .status("ACTIVE")
                    .documentType("DNI")
                    .documentNumber("12345678")
                    .email("vip@yanki.com")
                    .updatedAt(LocalDateTime.now())
                    .build();

            CustomerCardView cardView = CustomerCardView.builder()
                    .customerId("CUST001")
                    .hasCreditCard(true)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(vipProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(cardViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(cardView));
            when(accountPersistencePort.existsByCustomerIdAndType("CUST001", AccountType.SAVINGS))
                    .thenReturn(Single.just(false));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> {
                        Account a = inv.getArgument(0);
                        a.setId("ACC_VIP");
                        return Single.just(a);
                    });
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_OPEN");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishAccountOpened(any()))
                    .thenReturn(Completable.complete());

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            AccountResult result = observer.values().get(0);
            assertEquals("ACC_VIP", result.getAccountId());
            verify(cardViewPort).findByCustomerId("CUST001");
        }

        @Test
        @DisplayName("Debe rechazar cuenta ahorro VIP sin tarjeta de crédito (CP-024, CP-132)")
        void openVipSavingsAccount_withoutCreditCard() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("SAVINGS")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("5000.00"))
                    .channel("WEB")
                    .build();

            CustomerProfileView vipProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("PERSONAL")
                    .profile("VIP")
                    .status("ACTIVE")
                    .documentType("DNI")
                    .documentNumber("12345678")
                    .updatedAt(LocalDateTime.now())
                    .build();

            CustomerCardView cardView = CustomerCardView.builder()
                    .customerId("CUST001")
                    .hasCreditCard(false)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(vipProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(cardViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(cardView));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Open Account — Checking Enterprise (CU-02, CP-025, CP-026) ====================

    @Nested
    @DisplayName("Abrir cuenta corriente")
    class OpenCheckingAccountTests {

        @Test
        @DisplayName("Debe abrir cuenta corriente STANDARD para cliente ENTERPRISE (CP-025)")
        void openCheckingAccount_enterpriseStandard() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("CURRENT")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("5000.00"))
                    .channel("BRANCH")
                    .holders(List.of(
                            OpenAccountCommand.AccountHolderCommand.builder()
                                    .holderId("HOLDER001")
                                    .percentage(100)
                                    .principal(true)
                                    .build()
                    ))
                    .build();

            CustomerProfileView enterpriseProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("ENTERPRISE")
                    .profile("STANDARD")
                    .status("ACTIVE")
                    .documentType("RUC")
                    .documentNumber("20123456789")
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(enterpriseProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(accountPersistencePort.countByCustomerIdAndType("CUST001", AccountType.CURRENT))
                    .thenReturn(Single.just(0L));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> {
                        Account a = inv.getArgument(0);
                        a.setId("ACC_CHECKING");
                        return Single.just(a);
                    });
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_OPEN");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishAccountOpened(any()))
                    .thenReturn(Completable.complete());

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            AccountResult result = observer.values().get(0);
            assertEquals("ACC_CHECKING", result.getAccountId());
        }

        @Test
        @DisplayName("Debe abrir cuenta corriente PYME con tarjeta de crédito (CP-026, CP-133)")
        void openCheckingAccount_pyme_withCreditCard() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("CURRENT")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("5000.00"))
                    .channel("BRANCH")
                    .holders(List.of(
                            OpenAccountCommand.AccountHolderCommand.builder()
                                    .holderId("HOLDER001")
                                    .percentage(100)
                                    .principal(true)
                                    .build()
                    ))
                    .build();

            CustomerProfileView pymeProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("ENTERPRISE")
                    .profile("PYME")
                    .status("ACTIVE")
                    .documentType("RUC")
                    .documentNumber("20987654321")
                    .updatedAt(LocalDateTime.now())
                    .build();

            CustomerCardView cardView = CustomerCardView.builder()
                    .customerId("CUST001")
                    .hasCreditCard(true)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(pymeProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(cardViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(cardView));
            when(accountPersistencePort.countByCustomerIdAndType("CUST001", AccountType.CURRENT))
                    .thenReturn(Single.just(0L));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> {
                        Account a = inv.getArgument(0);
                        a.setId("ACC_PYME");
                        return Single.just(a);
                    });
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_OPEN");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishAccountOpened(any()))
                    .thenReturn(Completable.complete());

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            AccountResult result = observer.values().get(0);
            assertEquals("ACC_PYME", result.getAccountId());
        }

        @Test
        @DisplayName("Debe rechazar cuenta corriente PYME sin tarjeta de crédito (CP-027)")
        void openCheckingAccount_pyme_withoutCreditCard() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("CURRENT")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("5000.00"))
                    .holders(List.of(
                            OpenAccountCommand.AccountHolderCommand.builder()
                                    .holderId("HOLDER001")
                                    .percentage(100)
                                    .principal(true)
                                    .build()
                    ))
                    .build();

            CustomerProfileView pymeProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("ENTERPRISE")
                    .profile("PYME")
                    .status("ACTIVE")
                    .documentType("RUC")
                    .documentNumber("20987654321")
                    .updatedAt(LocalDateTime.now())
                    .build();

            CustomerCardView cardView = CustomerCardView.builder()
                    .customerId("CUST001")
                    .hasCreditCard(false)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(pymeProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(cardViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(cardView));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }

        @Test
        @DisplayName("Debe rechazar cuenta corriente para PERSONAL que ya tiene una (CP-028)")
        void openCheckingAccount_personal_alreadyHasOne() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("CURRENT")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("5000.00"))
                    .build();

            CustomerProfileView personalProfile = createPersonalProfile();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(personalProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(accountPersistencePort.countByCustomerIdAndType("CUST001", AccountType.CURRENT))
                    .thenReturn(Single.just(1L));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Open Account — Fixed Term (CU-02, CP-029, CP-030) ====================

    @Nested
    @DisplayName("Abrir cuenta plazo fijo")
    class OpenFixedTermAccountTests {

        @Test
        @DisplayName("Debe abrir cuenta plazo fijo para cliente PERSONAL (CP-029)")
        void openFixedTermAccount_personal() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("TERM_FIXED")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("10000.00"))
                    .termDays(90)
                    .allowedMovementDay(15)
                    .channel("BRANCH")
                    .build();

            CustomerProfileView personalProfile = createPersonalProfile();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(personalProfile));
            when(riskViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createCleanRiskView()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> {
                        Account a = inv.getArgument(0);
                        a.setId("ACC_FIXED");
                        return Single.just(a);
                    });
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_OPEN");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishAccountOpened(any()))
                    .thenReturn(Completable.complete());

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
            AccountResult result = observer.values().get(0);
            assertEquals("ACC_FIXED", result.getAccountId());
        }

        @Test
        @DisplayName("Debe rechazar cuenta plazo fijo para cliente ENTERPRISE (CP-030)")
        void openFixedTermAccount_enterpriseNotAllowed() {
            OpenAccountCommand command = OpenAccountCommand.builder()
                    .customerId("CUST001")
                    .accountType("TERM_FIXED")
                    .currency("PEN")
                    .initialAmount(new BigDecimal("10000.00"))
                    .termDays(90)
                    .build();

            CustomerProfileView enterpriseProfile = CustomerProfileView.builder()
                    .customerId("CUST001")
                    .customerType("ENTERPRISE")
                    .profile("STANDARD")
                    .status("ACTIVE")
                    .documentType("RUC")
                    .documentNumber("20123456789")
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(enterpriseProfile));

            TestObserver<AccountResult> observer = accountService.openAccount(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Deposit with Commission (CU-05, CP-038) ====================

    @Nested
    @DisplayName("Depósito con comisión por exceso")
    class DepositWithCommissionTests {

        @Test
        @DisplayName("Debe rechazar depósito por exceso de movimientos gratuitos (CP-038)")
        void deposit_withExcessMovementCommission() {
            DepositCommand command = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("200.00"))
                    .description("Depósito con comisión")
                    .channel("MOBILE")
                    .build();

            Account account = createTestAccount("ACC001");
            account.setMonthlyMovementCount(11);
            account.setCurrentMonth("2026-11");
            account.setMaxFreeMovementsPerMonth(10);
            account.setCommissionPerExcessMovement(new BigDecimal("2.50"));
            account.setBalance(new BigDecimal("1000.00"));

            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));

            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Withdrawal with Commission (CU-05, CP-041) ====================

    @Nested
    @DisplayName("Retiro con comisión por exceso")
    class WithdrawalWithCommissionTests {

        @Test
        @DisplayName("Debe rechazar retiro por exceso de movimientos gratuitos (CP-041)")
        void withdrawal_withExcessMovementCommission() {
            WithdrawalCommand command = WithdrawalCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("200.00"))
                    .description("Retiro con comisión")
                    .channel("ATM")
                    .build();

            Account account = createTestAccount("ACC001");
            account.setMonthlyMovementCount(11);
            account.setCurrentMonth("2026-11");
            account.setMaxFreeMovementsPerMonth(10);
            account.setCommissionPerExcessMovement(new BigDecimal("2.50"));
            account.setBalance(new BigDecimal("1000.00"));

            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));

            TestObserver<MovementResult> observer = accountService.withdrawal(command).test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Balance — Account Not Found (CU-08, CP-053) ====================

    @Nested
    @DisplayName("Consulta de saldo — errores")
    class GetBalanceErrorTests {

        @Test
        @DisplayName("Debe lanzar error si la cuenta no existe (CP-053)")
        void getBalance_accountNotFound() {
            when(cachePort.getBalance("UNKNOWN"))
                    .thenReturn(Single.just(BigDecimal.ZERO));
            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            TestObserver<BalanceResult> observer = accountService.getBalance("UNKNOWN").test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Transfer — Commission (CU-12, CP-067, CP-068) ====================

    @Nested
    @DisplayName("Liquidar transferencia con reglas de comisión")
    class SettleTransferCommissionTests {

        @Test
        @DisplayName("Debe procesar transferencia desde cuenta corriente sin comisión (CP-068)")
        void settleTransfer_checkingNoCommission() {
            TransferCommand command = TransferCommand.builder()
                    .transferId("TRF002")
                    .sourceAccountId("ACC_CHECKING")
                    .targetAccountId("ACC002")
                    .amount(new BigDecimal("100.00"))
                    .description("Transferencia desde corriente")
                    .requestedBy("CUST001")
                    .occurredAt(LocalDateTime.now())
                    .build();

            when(idempotencyPort.checkAndMarkProcessed(any()))
                    .thenReturn(Single.just(false));

            Account source = Account.builder()
                    .id("ACC_CHECKING")
                    .customerId("CUST001")
                    .accountNumber("ACC123456789")
                    .type(AccountType.CURRENT)
                    .status(AccountStatus.OPEN)
                    .currency("PEN")
                    .balance(new BigDecimal("1000.00"))
                    .openedAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .monthlyMovementCount(0)
                    .currentMonth("2026-11")
                    .maxFreeMovementsPerMonth(null)
                    .commissionPerExcessMovement(BigDecimal.ZERO)
                    .maintenanceCommission(new BigDecimal("15.00"))
                    .build();

            Account target = createTestAccount("ACC002");
            target.setCustomerId("CUST002");

            when(accountPersistencePort.findById("ACC_CHECKING"))
                    .thenReturn(Maybe.just(source));
            when(accountPersistencePort.findById("ACC002"))
                    .thenReturn(Maybe.just(target));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        m.setId("MOV_TRF_CHECKING");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            when(eventPublisherPort.publishTransferSettled(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance(any());

            TestObserver<Void> observer = accountService.settleTransfer(command).test();

            observer.assertComplete();
            observer.assertNoErrors();
        }
    }

    // ==================== Find Account — Not Found (CU-08, CP-053) ====================

    @Nested
    @DisplayName("Buscar cuenta — errores")
    class FindAccountErrorTests {

        @Test
        @DisplayName("Debe lanzar error si la cuenta no existe (CP-053)")
        void findById_notFound() {
            when(accountPersistencePort.findById("UNKNOWN"))
                    .thenReturn(Maybe.empty());

            TestObserver<AccountResult> observer = accountService.findById("UNKNOWN").test();

            observer.assertError(BusinessException.class);
        }
    }

    // ==================== Close Account — Already Closed (CU-02) ====================

    @Nested
    @DisplayName("Cerrar cuenta — casos adicionales")
    class CloseAccountAdditionalTests {

        @Test
        @DisplayName("Debe rechazar cierre si la cuenta ya está cerrada")
        void closeAccount_alreadyClosed() {
            Account account = createTestAccount("ACC001");
            account.setStatus(AccountStatus.CLOSED);
            account.setBalance(BigDecimal.ZERO);
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));

            TestObserver<Void> observer = accountService.closeAccount("ACC001").test();

            observer.assertError(BusinessException.class);
        }
    }
}
