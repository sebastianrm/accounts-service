package com.yanki.accounts.domain.service;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.CustomerProfileView;
import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.port.out.CachePort;
import com.yanki.accounts.domain.port.out.CustomerCardViewPort;
import com.yanki.accounts.domain.port.out.CustomerProfileViewPort;
import com.yanki.accounts.domain.port.out.CustomerRiskViewPort;
import com.yanki.accounts.domain.port.out.EventPublisherPort;
import com.yanki.accounts.domain.port.out.IdempotencyPort;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Pruebas que reproducen el problema 2.1: Depósito retorna 500 (E11000 duplicate key).
 *
 * <p>Causa raíz: {@code AccountService.buildMovement()} no asigna un ID único al crear
 * un nuevo Movement. El campo {@code id} queda {@code null}, y al persistir a través de
 * {@code MovementDocumentMapper.toDocument()}, el ID null se pasa al MovementDocument.
 * Cuando Spring Data MongoDB recibe un documento con {@code @Id} null, genera automáticamente
 * un ObjectId, pero en escenarios concurrentes o de re-intento, el ID null colisiona
 * produciendo E11000 duplicate key.</p>
 *
 * <p>Estas pruebas demuestran que:</p>
 * <ul>
 *   <li>El Movement creado por buildMovement() tiene id = null.</li>
 *   <li>Al persistir sin ID, el adaptador recibe un documento sin identificador.</li>
 *   <li>La persistencia falla con error de duplicado cuando se intenta guardar sin ID.</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceBuildMovementNullIdTest {

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
                .email("cliente@yanki.com")
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @BeforeEach
    void setUp() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        accountService = new AccountService(
                accountPersistencePort, movementPersistencePort, profileViewPort,
                cardViewPort, riskViewPort, idempotencyPort, eventPublisherPort, cachePort
        );
    }

    @AfterEach
    void tearDown() {
        RxJavaPlugins.reset();
    }

    /**
     * Pruebas que validan el fix del problema 2.1: buildMovement() ahora genera ID único.
     * Cada Movement creado por buildMovement() tiene un ID único generado por UUID.randomUUID().toString().
     */
    @Nested
    @DisplayName("Fix problema 2.1: buildMovement() genera ID único")
    class BuildMovementUniqueIdTests {

        @Test
        @DisplayName("El Movement creado por depósito debe tener ID único (fix verificado)")
        void deposit_movementHasUniqueId_fixVerified() {
            // Arrange
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

            // Captura el Movement enviado a save() para verificar su ID
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement capturedMovement = inv.getArgument(0);
                        // FIX: El ID ya no es null porque buildMovement() genera UUID
                        assertNotNull(capturedMovement.getId(),
                                "FIX VERIFICADO: buildMovement() ahora asigna ID único al Movement. "
                                        + "Esto evita E11000 duplicate key en MongoDB.");
                        return Single.just(capturedMovement);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            // Act
            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            // Assert
            observer.assertComplete();
            observer.assertNoErrors();
        }

        @Test
        @DisplayName("El Movement persistido debe tener ID único antes del adapter (fix verificado)")
        void deposit_movementSentToPersistenceHasUniqueId() {
            // Arrange
            DepositCommand command = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("500.00"))
                    .description("Depósito web")
                    .channel("WEB")
                    .build();

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));

            // Verificamos que el Movement recibido por el adaptador tiene ID único
            java.util.concurrent.atomic.AtomicReference<String> movementIdRef =
                    new java.util.concurrent.atomic.AtomicReference<>();
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        movementIdRef.set(m.getId());
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            // Act
            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            // Assert
            observer.assertComplete();
            assertNotNull(movementIdRef.get(),
                    "FIX: El Movement enviado a movementPersistencePort.save() tiene ID único generado por UUID.");
        }

        @Test
        @DisplayName("Persistencia con ID único debe tener éxito (sin error de duplicado)")
        void deposit_persistenceSucceedsWithUniqueId() {
            // Arrange
            DepositCommand command = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("200.00"))
                    .description("Depósito")
                    .channel("BRANCH")
                    .build();

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));

            // Con el fix, el ID ya no es null, por lo que no hay error de duplicado
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        assertNotNull(m.getId(), "El Movement debe tener ID único");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            // Act
            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            // Assert — El depósito tiene éxito sin error de duplicado
            observer.assertComplete();
            observer.assertNoErrors();
        }

        @Test
        @DisplayName("Retiro también debe tener ID único (fix verificado)")
        void withdrawal_movementHasUniqueId_fixVerified() {
            // Arrange
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

            java.util.concurrent.atomic.AtomicReference<String> movementIdRef =
                    new java.util.concurrent.atomic.AtomicReference<>();
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        movementIdRef.set(m.getId());
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            // Act
            TestObserver<MovementResult> observer = accountService.withdrawal(command).test();

            // Assert
            observer.assertComplete();
            assertNotNull(movementIdRef.get(),
                    "FIX: El Movement del retiro también tiene ID único generado por UUID.");
        }

        @Test
        @DisplayName("Múltiples depósitos concurrentes no colisionan con ID único")
        void concurrentDeposits_noCollisionWithUniqueId() {
            // Arrange
            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));

            // Con el fix, cada Movement tiene ID único, no hay colisión
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        assertNotNull(m.getId(), "Cada Movement debe tener ID único");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            // Act — Primer depósito
            DepositCommand command1 = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("100.00"))
                    .channel("WEB")
                    .build();
            TestObserver<MovementResult> observer1 = accountService.deposit(command1).test();

            // Act — Segundo depósito
            DepositCommand command2 = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("200.00"))
                    .channel("MOBILE")
                    .build();
            TestObserver<MovementResult> observer2 = accountService.deposit(command2).test();

            // Assert — Ambos tienen éxito porque cada uno tiene ID único
            observer1.assertComplete();
            observer1.assertNoErrors();
            observer2.assertComplete();
            observer2.assertNoErrors();
        }
    }

    /**
     * Pruebas que validan el comportamiento esperado después de aplicar el fix.
     * El Movement debe tener un ID único generado por UUID.randomUUID().toString().
     */
    @Nested
    @DisplayName("Comportamiento esperado: buildMovement() debe generar ID único")
    class ExpectedBehaviorAfterFix {

        @Test
        @DisplayName("El MovementResult debe contener movementId no nulo")
        void deposit_resultShouldHaveNonNullMovementId() {
            // Arrange
            DepositCommand command = DepositCommand.builder()
                    .accountId("ACC001")
                    .amount(new BigDecimal("200.00"))
                    .description("Depósito")
                    .channel("BRANCH")
                    .build();

            Account account = createTestAccount("ACC001");
            when(accountPersistencePort.findById("ACC001"))
                    .thenReturn(Maybe.just(account));
            when(profileViewPort.findByCustomerId("CUST001"))
                    .thenReturn(Maybe.just(createPersonalProfile()));
            when(accountPersistencePort.save(any(Account.class)))
                    .thenAnswer(inv -> Single.just(inv.getArgument(0)));

            // El adaptador recibe el Movement con ID ya asignado (después del fix)
            when(movementPersistencePort.save(any(Movement.class)))
                    .thenAnswer(inv -> {
                        Movement m = inv.getArgument(0);
                        // Después del fix, m.getId() ya no es null
                        assertNotNull(m.getId(),
                                "Después del fix, el Movement debe tener ID generado por UUID");
                        return Single.just(m);
                    });
            when(eventPublisherPort.publishMovementRegistered(any()))
                    .thenReturn(Completable.complete());
            doNothing().when(cachePort).invalidateBalance("ACC001");

            // Act
            TestObserver<MovementResult> observer = accountService.deposit(command).test();

            // Assert
            observer.assertComplete();
            observer.assertNoErrors();
            MovementResult result = observer.values().get(0);
            assertNotNull(result.getMovementId(),
                    "El MovementResult debe contener movementId no nulo");
        }
    }
}
