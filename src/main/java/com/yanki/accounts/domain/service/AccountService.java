package com.yanki.accounts.domain.service;

import com.yanki.accounts.domain.event.AccountClosedEvent;
import com.yanki.accounts.domain.event.AccountMovementRegisteredEvent;
import com.yanki.accounts.domain.event.AccountOpenedEvent;
import com.yanki.accounts.domain.event.DebitPaymentSettledEvent;
import com.yanki.accounts.domain.event.Topics;
import com.yanki.accounts.domain.event.TransferSettledEvent;
import com.yanki.accounts.domain.exception.BusinessException;
import com.yanki.accounts.domain.exception.ErrorCodes;
import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountHolder;
import com.yanki.accounts.domain.model.BalanceResult;
import com.yanki.accounts.domain.model.AccountSigner;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.CustomerProfileView;
import com.yanki.accounts.domain.model.CustomerRiskView;
import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.in.CloseAccountUseCase;
import com.yanki.accounts.domain.port.in.DepositUseCase;
import com.yanki.accounts.domain.port.in.OpenAccountUseCase;
import com.yanki.accounts.domain.port.in.SettleDebitPaymentUseCase;
import com.yanki.accounts.domain.port.in.SettleTransferUseCase;
import com.yanki.accounts.domain.port.in.WithdrawUseCase;
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
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Servicio principal para la gestión de cuentas bancarias en la plataforma Yanki.
 *
 * <p>Este servicio maneja toda la lógica de negocio de los productos pasivos (ahorro, corriente, plazo fijo)
 * utilizando principios de reactividad pura con RxJava 3, evitando {@code block()}, {@code subscribe()} o {@code Thread.sleep()}.</p>
 *
 * <p>Reglas de negocio implementadas:</p>
 * <ul>
 *   <li>Límites por tipo de cliente (PERSONAL/ENTERPRISE) y tipo de cuenta.</li>
 *   <li>Requisito de tarjeta de crédito para perfiles VIP (PERSONAL) y PYME (ENTERPRISE).</li>
 *   <li>Bloqueo de cuentas por deuda vencida (customer_risk_view).</li>
 *   <li>Reglas especiales para cuentas a plazo fijo (día permitido de movimiento).</li>
 *   <li>Comisiones de mantenimiento y exceso de movimientos.</li>
 *   <li>Idempotencia en liquidaciones (débito, transferencias).</li>
 * </ul>
 *
 * <p>El servicio es productor y consumidor clave en la arquitectura event-driven (Parte III):</p>
 * <ul>
 *   <li>Publica eventos: account.opened, account.closed, movement.registered, debit.payment.settled, transfer.settled</li>
 *   <li>Consume eventos: customer.registered/updated/deleted, card.opened/closed, debit.payment.requested, transfer.requested, credit.overdue</li>
 * </ul>
 *
 * @see <a href="https://github.com/yanki/accounts-service">Repository</a>
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Service
@RequiredArgsConstructor
public class AccountService implements
        OpenAccountUseCase,
        DepositUseCase,
        WithdrawUseCase,
        CloseAccountUseCase,
        SettleDebitPaymentUseCase,
        SettleTransferUseCase {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountPersistencePort accountPersistencePort;
    private final MovementPersistencePort movementPersistencePort;
    private final CustomerProfileViewPort profileViewPort;
    private final CustomerCardViewPort cardViewPort;
    private final CustomerRiskViewPort riskViewPort;
    private final IdempotencyPort idempotencyPort;
    private final EventPublisherPort eventPublisherPort;
    private final CachePort cachePort;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * Opens a new bank account.
     * Validates customer eligibility, checks requirements, creates the account, and publishes an event.
     *
     * @param command Contains the opening data
     * @return {@link Single} con {@link AccountResult}
     */
    @Override
    public Single<AccountResult> execute(OpenAccountCommand command) {
        return Single.defer(() -> {
            log.info("Iniciando apertura de cuenta para cliente: {}, tipo: {}, monto: {}, canal: {}",
                    command.getCustomerId(), command.getAccountType(), command.getInitialAmount(), command.getChannel());

            try {
                AccountType accountType = parseAccountType(command.getAccountType());
                log.debug("Tipo de cuenta parseado: {}", accountType);

                return getProfileViewOrError(command.getCustomerId())
                        .flatMap(profile -> {
                            log.debug("Perfil del cliente obtenido: tipo={}, perfil={}", profile.getCustomerType(), profile.getProfile());
                            return validateCustomerForAccountOpening(profile, accountType, command);
                        })
                        .flatMap(profile -> checkRiskView(command.getCustomerId())
                                .flatMap(hasRisk -> {
                                    if (hasRisk) {
                                        log.warn("Apertura de cuenta rechazada: cliente {} tiene deuda vencida", command.getCustomerId());
                                        return Single.error(new BusinessException(
                                                ErrorCodes.CUSTOMER_HAS_OVERDUE_DEBT,
                                                "Cliente tiene deuda vencida, no puede abrir cuenta"
                                        ));
                                    }
                                    log.debug("Verificación de riesgo OK para cliente: {}", command.getCustomerId());
                                    return Single.just(profile);
                                }))
                        .flatMap(profile -> {
                            log.debug("Verificando requisito de tarjeta de crédito para perfil: {}, tipo cuenta: {}", profile.getProfile(), accountType);
                            return checkCreditCardRequirement(profile, accountType);
                        })
                        .flatMap(profile -> {
                            log.debug("Verificando límites de cuenta para cliente: {}, tipo: {}", command.getCustomerId(), accountType);
                            return checkAccountLimits(profile, accountType);
                        })
                        .flatMap(profile -> createAndSaveAccount(command, accountType, profile))
                        .flatMap(accountResult -> {
                            log.info("Cuenta creada exitosamente: accountId={}, customerId={}, tipo={}, saldo={}",
                                    accountResult.getAccountId(), accountResult.getCustomerId(), accountResult.getAccountType(), accountResult.getBalance());
                            return publishAccountOpenedEvent(accountResult)
                                    .andThen(Single.just(accountResult));
                        })
                        .onErrorResumeNext(throwable -> {
                            log.error("Error al abrir cuenta para cliente: {}, tipo: {} - {}",
                                    command.getCustomerId(), command.getAccountType(), throwable.getMessage());
                            return Single.error(throwable);
                        })
                        .subscribeOn(Schedulers.io());
            } catch (Exception e) {
                log.error("Error inesperado al parsear tipo de cuenta para cliente: {} - {}",
                        command.getCustomerId(), e.getMessage(), e);
                return Single.error(e);
            }
        });
    }

    /**
     * Finds an account by its ID.
     * 
     * @param id The account's unique identifier
     * @return A Single emitting the account result or an error if not found
     */
    public Single<AccountResult> findById(String id) {
        log.debug("Buscando cuenta por ID: {}", id);
        return accountPersistencePort.findById(id)
                .switchIfEmpty(Single.error(() -> {
                    log.warn("Cuenta no encontrada por ID: {}", id);
                    return new BusinessException(
                            ErrorCodes.ACCOUNT_NOT_FOUND,
                            "Cuenta no encontrada: " + id
                    );
                }))
                .map(account -> {
                    log.debug("Cuenta encontrada: id={}, tipo={}, estado={}", id, account.getType(), account.getStatus());
                    return toAccountResult(account);
                })
                .onErrorResumeNext(throwable -> {
                    log.error("Error al buscar cuenta por ID: {} - {}", id, throwable.getMessage());
                    return Single.error(throwable);
                })
                .subscribeOn(Schedulers.io());
    }

    /**
     * Valida una cuenta para uso en transferencias inter-servicio.
     *
     * <p>Verifica que la cuenta exista, esté operativa (OPEN) y consulta si el cliente
     * tiene deuda vencida. Retorna toda la información necesaria para que transfers-service
     * pueda decidir si la cuenta es válida como origen o destino.</p>
     *
     * @param accountId Identificador único de la cuenta a validar
     * @return A Single emitting the validation result with account details and risk status
     */
    public Single<AccountValidationResult> validateForTransfer(String accountId) {
        log.debug("Validando cuenta para transferencia: {}", accountId);
        return accountPersistencePort.findById(accountId)
                .switchIfEmpty(Single.error(() -> {
                    log.warn("Cuenta no encontrada para validación de transferencia: {}", accountId);
                    return new BusinessException(
                            ErrorCodes.ACCOUNT_NOT_FOUND,
                            "Cuenta no encontrada: " + accountId
                    );
                }))
                .flatMap(account -> {
                    log.debug("Cuenta encontrada para validación: id={}, tipo={}, estado={}",
                            accountId, account.getType(), account.getStatus());
                    boolean isValid = account.canOperate();
                    if (!isValid) {
                        log.warn("Cuenta no operativa para transferencia: id={}, estado={}",
                                accountId, account.getStatus());
                    }
                    // Consulta el riesgo del cliente para verificar deuda vencida
                    return checkRiskView(account.getCustomerId())
                            .map(hasOverdueDebt -> {
                                log.debug("Validación de transferencia completada: accountId={}, valid={}, hasOverdueDebt={}",
                                        accountId, isValid, hasOverdueDebt);
                                return AccountValidationResult.builder()
                                        .valid(isValid)
                                        .accountId(account.getId())
                                        .customerId(account.getCustomerId())
                                        .status(account.getStatus().name())
                                        .balance(account.getBalance())
                                        .productType(account.getType().name())
                                        .maxFreeMovements(account.getMaxFreeMovementsPerMonth())
                                        .commissionPerExcessMovement(account.getCommissionPerExcessMovement())
                                        .hasOverdueDebt(hasOverdueDebt)
                                        .build();
                            });
                })
                .onErrorResumeNext(throwable -> {
                    log.error("Error al validar cuenta para transferencia: {} - {}", accountId, throwable.getMessage());
                    return Single.error(throwable);
                })
                .subscribeOn(Schedulers.io());
    }

    /**
     * Busca cuentas aplicando los filtros proporcionados mediante el patrón Strategy.
     * 
     * <p>Soporta cualquier combinación de filtros opcionales: customerId, type, status.
     * Si no se proporciona ningún filtro, devuelve todas las cuentas.</p>
     *
     * @param criteria Criterios de filtrado con cualquier combinación de customerId, type, status
     * @return A Flowable emitting the filtered account results
     */
    public Flowable<AccountResult> findByFilters(AccountFilterCriteria criteria) {
        log.debug("Buscando cuentas con filtros: customerId={}, type={}, status={}",
                criteria.getCustomerId(), criteria.getType(), criteria.getStatus());
        try {
            com.yanki.accounts.domain.service.strategy.AccountFilterContext context =
                    new com.yanki.accounts.domain.service.strategy.AccountFilterContext(
                            accountPersistencePort, this::toAccountResult);
            return context.find(criteria)
                    .doOnComplete(() -> log.debug("Búsqueda de cuentas con filtros completada"))
                    .doOnError(throwable -> log.error("Error al buscar cuentas con filtros - {}", throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado al buscar cuentas con filtros - {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Busca movimientos de una cuenta aplicando los filtros proporcionados mediante el patrón Strategy.
     *
     * <p>Soporta cualquier combinación de filtros opcionales: from, to, type.
     * Si no se proporciona ningún filtro, devuelve todos los movimientos de la cuenta.</p>
     *
     * @param accountId Identificador de la cuenta
     * @param criteria Criterios de filtrado con cualquier combinación de from, to, type
     * @return A Flowable emitting the filtered movements
     */
    public Flowable<Movement> findMovements(String accountId, MovementFilterCriteria criteria) {
        log.debug("Buscando movimientos para cuenta: {}, desde={}, hasta={}, tipo={}",
                accountId, criteria.getFrom(), criteria.getTo(), criteria.getType());
        return accountPersistencePort.findById(accountId)
                .switchIfEmpty(Single.error(() -> {
                    log.warn("Cuenta no encontrada para buscar movimientos: {}", accountId);
                    return new BusinessException(
                            ErrorCodes.ACCOUNT_NOT_FOUND,
                            "Cuenta no encontrada: " + accountId
                    );
                }))
                .toObservable()
                .flatMap(account -> {
                    log.debug("Cuenta encontrada para movimientos: id={}, tipo={}", accountId, account.getType());
                    com.yanki.accounts.domain.service.strategy.MovementFilterContext context =
                            new com.yanki.accounts.domain.service.strategy.MovementFilterContext(
                                    movementPersistencePort, accountId);
                    return context.find(criteria).toObservable();
                })
                .toFlowable(BackpressureStrategy.BUFFER)
                .doOnError(throwable -> log.error("Error al buscar movimientos para cuenta: {} - {}", accountId, throwable.getMessage()))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Retrieves the balance of an account.
     * 
     * @param accountId The account's unique identifier
     * @return A Single emitting the balance result with cached flag
     */
    public Single<BalanceResult> getBalance(String accountId) {
        log.debug("Consultando saldo de la cuenta: {}", accountId);
        return cachePort.getBalance(accountId)
                .flatMap(cachedBalance -> {
                    if (cachedBalance.compareTo(BigDecimal.ZERO) != 0) {
                        log.debug("Saldo encontrado en caché para cuenta {}: {}", accountId, cachedBalance);
                        return Single.just(new BalanceResult(cachedBalance, true));
                    }
                    log.debug("Saldo no encontrado en caché para cuenta {}, consultando MongoDB", accountId);
                    return accountPersistencePort.findById(accountId)
                            .map(account -> {
                                BigDecimal balance = account.getBalance();
                                log.debug("Saldo obtenido de MongoDB para cuenta {}: {}", accountId, balance);
                                return new BalanceResult(balance, false);
                            })
                            .doOnSuccess(balanceResult -> cachePort.setBalance(accountId, balanceResult.getBalance()))
                            .switchIfEmpty(Single.error(() -> {
                                log.warn("Cuenta no encontrada para consultar saldo: {}", accountId);
                                return new BusinessException(
                                        ErrorCodes.ACCOUNT_NOT_FOUND,
                                        "Cuenta no encontrada: " + accountId
                                );
                            }));
                })
                .onErrorResumeNext(throwable -> {
                    log.error("Error al consultar saldo de cuenta: {} - {}", accountId, throwable.getMessage());
                    return Single.error(throwable);
                })
                .subscribeOn(Schedulers.io());
    }

    /**
     * Executes a deposit into the account.
     * 
     * @param command Contains the deposit data
     * @return A Single emitting the movement result
     */
    @Override
    public Single<MovementResult> execute(DepositCommand command) {
        log.info("Procesando depósito en cuenta: {}, monto: {}, canal: {}",
                command.getAccountId(), command.getAmount(), command.getChannel());
        return Single.defer(() ->
                processMovement(
                        command.getAccountId(),
                        command.getAmount(),
                        MovementType.DEPOSIT,
                        command.getDescription(),
                        command.getChannel(),
                        command.getReferenceId(),
                        BigDecimal.ZERO
                ).doOnSuccess(result -> log.info("Depósito exitoso en cuenta: {}, monto: {}, nuevo saldo: {}",
                        command.getAccountId(), command.getAmount(), result.getBalanceAfter()))
                .doOnError(throwable -> log.error("Error en depósito cuenta: {}, monto: {} - {}",
                        command.getAccountId(), command.getAmount(), throwable.getMessage()))
                .subscribeOn(Schedulers.io())
        );
    }

    /**
     * Executes a withdrawal from the account.
     * 
     * @param command Contains the withdrawal data
     * @return A Single emitting the movement result
     */
    @Override
    public Single<MovementResult> execute(WithdrawalCommand command) {
        log.info("Procesando retiro de cuenta: {}, monto: {}, canal: {}",
                command.getAccountId(), command.getAmount(), command.getChannel());
        return Single.defer(() ->
                processMovement(
                        command.getAccountId(),
                        command.getAmount().negate(),
                        MovementType.WITHDRAWAL,
                        command.getDescription(),
                        command.getChannel(),
                        command.getReferenceId(),
                        BigDecimal.ZERO
                ).doOnSuccess(result -> log.info("Retiro exitoso de cuenta: {}, monto: {}, nuevo saldo: {}",
                        command.getAccountId(), command.getAmount(), result.getBalanceAfter()))
                .doOnError(throwable -> log.error("Error en retiro cuenta: {}, monto: {} - {}",
                        command.getAccountId(), command.getAmount(), throwable.getMessage()))
                .subscribeOn(Schedulers.io())
        );
    }

    /**
     * Closes an account.
     * 
     * @param accountId The account's unique identifier
     * @return A Completable that signals the completion of the operation
     */
    @Override
    public Completable execute(String accountId) {
        log.info("Iniciando cierre de cuenta: {}", accountId);
        try {
            return accountPersistencePort.findById(accountId)
                    .switchIfEmpty(Single.error(() -> {
                        log.warn("Cuenta no encontrada para cerrar: {}", accountId);
                        return new BusinessException(
                                ErrorCodes.ACCOUNT_NOT_FOUND,
                                "Cuenta no encontrada: " + accountId
                        );
                    }))
                    .flatMap(account -> {
                        log.debug("Cuenta encontrada para cerrar: id={}, estado={}, saldo={}",
                                accountId, account.getStatus(), account.getBalance());
                        return profileViewPort.findByCustomerId(account.getCustomerId())
                                .switchIfEmpty(Single.error(() -> {
                                    log.warn("Cliente no encontrado para cierre de cuenta: {}", account.getCustomerId());
                                    return new BusinessException(
                                            ErrorCodes.CUSTOMER_NOT_FOUND,
                                            "Cliente no encontrado: " + account.getCustomerId()
                                    );
                                }))
                                .flatMap(profile -> {
                                    if (!profile.canOperate()) {
                                        log.warn("Cliente inactivo para cierre de cuenta: {}", account.getCustomerId());
                                        return Single.error(new BusinessException(
                                                ErrorCodes.CUSTOMER_INACTIVE,
                                                "Cliente no está activo"
                                        ));
                                    }
                                    return Single.just(account);
                                });
                    })
                    .flatMapCompletable(account -> {
                        if (!AccountStatus.OPEN.equals(account.getStatus())) {
                            log.warn("Cuenta ya cerrada o bloqueada: id={}, estado={}", accountId, account.getStatus());
                            return Completable.error(new BusinessException(
                                    ErrorCodes.ACCOUNT_ALREADY_CLOSED,
                                    "Cuenta ya está cerrada o bloqueada"
                            ));
                        }
                        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                            log.warn("No se puede cerrar cuenta con saldo no cero: id={}, saldo={}", accountId, account.getBalance());
                            return Completable.error(new BusinessException(
                                    ErrorCodes.ACCOUNT_CANNOT_CLOSE_BALANCE_NOT_ZERO,
                                    "No se puede cerrar cuenta con saldo distinto de 0"
                            ));
                        }
                        log.info("Cerrando cuenta: {}", accountId);
                        account.setStatus(AccountStatus.CLOSED);
                        account.setClosedAt(LocalDateTime.now());
                        account.setUpdatedAt(LocalDateTime.now());
                        return accountPersistencePort.save(account)
                                .ignoreElement()
                                .andThen(Completable.fromAction(() -> {
                                    cachePort.invalidateBalance(accountId);
                                    log.info("Cuenta cerrada exitosamente: {}", accountId);
                                }))
                                .andThen(publishAccountClosedEvent(accountId));
                    })
                    .doOnError(throwable -> log.error("Error al cerrar cuenta: {} - {}", accountId, throwable.getMessage()))
                    .subscribeOn(Schedulers.io());
        } catch (Exception e) {
            log.error("Error inesperado al cerrar cuenta: {} - {}", accountId, e.getMessage(), e);
            return Completable.error(e);
        }
    }

    /**
     * Settles a debit payment.
     * 
     * @param command Contains the debit payment data
     * @return A Completable that signals the completion of the operation
     */
    @Override
    public Completable execute(DebitPaymentCommand command) {
        log.info("Liquidando pago débito: paymentId={}, cardId={}, accountId={}, monto={}",
                command.getPaymentId(), command.getCardId(), command.getAccountId(), command.getAmount());
        return Completable.defer(() -> {
            String idempotencyKey = buildIdempotencyKey(
                    Topics.DEBIT_PAYMENT_REQUESTED,
                    command.getPaymentId(),
                    command.getOccurredAt()
            );
            log.debug("Clave de idempotencia para pago débito: {}", idempotencyKey);

            try {
                return idempotencyPort.checkAndMarkProcessed(idempotencyKey)
                        .flatMapCompletable(processed -> {
                            if (processed) {
                                log.debug("Pago débito ya procesado (idempotente): {}", command.getPaymentId());
                                return Completable.complete();
                            }

                            return validateAndDebitAccount(
                                    command.getAccountId(),
                                    command.getAmount(),
                                    command.getPaymentId()
                            ).flatMapCompletable(movements -> {
                                Movement movement = movements.get(0);
                                log.info("Pago débito liquidado: paymentId={}, accountId={}, monto={}",
                                        command.getPaymentId(), command.getAccountId(), command.getAmount());
                                AccountMovementRegisteredEvent movementEvent = buildMovementEvent(
                                        movement,
                                        command.getAccountId(),
                                        command.getCustomerId(),
                                        MovementType.DEBIT_PAYMENT,
                                        command.getAmount(),
                                        BigDecimal.ZERO
                                );
                                return eventPublisherPort.publishMovementRegistered(movementEvent)
                                        .andThen(publishDebitPaymentSettledEvent(
                                                command.getPaymentId(),
                                                command.getCardId(),
                                                "SETTLED",
                                                null
                                        ));
                            }).onErrorResumeNext(throwable -> {
                                log.error("Error liquidando pago débito: paymentId={} - {}",
                                        command.getPaymentId(), throwable.getMessage());
                                return publishDebitPaymentSettledEvent(
                                        command.getPaymentId(),
                                        command.getCardId(),
                                        "REJECTED",
                                        throwable.getMessage()
                                ).andThen(Completable.error(throwable));
                            });
                        })
                        .subscribeOn(Schedulers.io());
            } catch (Exception e) {
                log.error("Error inesperado liquidando pago débito: paymentId={} - {}",
                        command.getPaymentId(), e.getMessage(), e);
                return Completable.error(e);
            }
        });
    }

    /**
     * Settles a transfer.
     * 
     * @param command Contains the transfer data
     * @return A Completable that signals the completion of the operation
     */
    public Completable settleTransfer(TransferCommand command) {
        log.info("Liquidando transferencia: transferId={}, origen={}, destino={}, monto={}",
                command.getTransferId(), command.getSourceAccountId(), command.getTargetAccountId(), command.getAmount());
        return Completable.defer(() -> {
            String idempotencyKey = buildIdempotencyKey(
                    Topics.TRANSFER_REQUESTED,
                    command.getTransferId(),
                    command.getOccurredAt()
            );
            log.debug("Clave de idempotencia para transferencia: {}", idempotencyKey);

            try {
                return idempotencyPort.checkAndMarkProcessed(idempotencyKey)
                        .flatMapCompletable(processed -> {
                            if (processed) {
                                log.debug("Transferencia ya procesada (idempotente): {}", command.getTransferId());
                                return Completable.complete();
                            }

                            if (command.getSourceAccountId().equals(command.getTargetAccountId())) {
                                log.warn("Transferencia rechazada: misma cuenta origen y destino: {}", command.getTransferId());
                                return Completable.error(new BusinessException(
                                        ErrorCodes.TRANSFER_SAME_ACCOUNT,
                                        "No se puede transferir a la misma cuenta"
                                ));
                            }

                            return validateAndTransfer(
                                    command.getSourceAccountId(),
                                    command.getTargetAccountId(),
                                    command.getAmount(),
                                    command.getTransferId(),
                                    command.getDescription()
                            ).flatMapCompletable(movements -> {
                                log.info("Transferencia liquidada: transferId={}, origen={}, destino={}, monto={}",
                                        command.getTransferId(), command.getSourceAccountId(), command.getTargetAccountId(), command.getAmount());
                                AccountMovementRegisteredEvent outEvent = buildMovementEvent(
                                        movements.get(0),
                                        command.getSourceAccountId(),
                                        movements.get(0).getCustomerId(),
                                        MovementType.TRANSFER_OUT,
                                        command.getAmount(),
                                        BigDecimal.ZERO
                                );

                                AccountMovementRegisteredEvent inEvent = buildMovementEvent(
                                        movements.get(1),
                                        command.getTargetAccountId(),
                                        movements.get(1).getCustomerId(),
                                        MovementType.TRANSFER_IN,
                                        command.getAmount(),
                                        BigDecimal.ZERO
                                );

                                return eventPublisherPort.publishMovementRegistered(outEvent)
                                        .andThen(eventPublisherPort.publishMovementRegistered(inEvent))
                                        .andThen(publishTransferSettledEvent(
                                                command.getTransferId(),
                                                "COMPLETED",
                                                null
                                        ));
                            }).onErrorResumeNext(throwable -> {
                                log.error("Error liquidando transferencia: transferId={} - {}",
                                        command.getTransferId(), throwable.getMessage());
                                return publishTransferSettledEvent(
                                        command.getTransferId(),
                                        "REJECTED",
                                        throwable.getMessage()
                                ).andThen(Completable.error(throwable));
                            });
                        })
                        .subscribeOn(Schedulers.io());
            } catch (Exception e) {
                log.error("Error inesperado liquidando transferencia: transferId={} - {}",
                        command.getTransferId(), e.getMessage(), e);
                return Completable.error(e);
            }
        });
    }

    private Single<CustomerProfileView> getProfileViewOrError(String customerId) {
        return profileViewPort.findByCustomerId(customerId)
                .switchIfEmpty(Single.error(() -> new BusinessException(
                        ErrorCodes.CUSTOMER_NOT_FOUND,
                        "Cliente no encontrado: " + customerId
                )));
    }

    private Single<CustomerProfileView> validateCustomerForAccountOpening(
            CustomerProfileView profile,
            AccountType accountType,
            OpenAccountCommand command
    ) {
        if (!profile.canOperate()) {
            return Single.error(new BusinessException(ErrorCodes.CUSTOMER_INACTIVE, "Cliente no está activo"));
        }

        if (profile.isPersonal()) {
            if (AccountType.TERM_FIXED.equals(accountType)
                    && (command.getTermDays() == null || command.getTermDays() <= 0)) {
                return Single.error(new BusinessException(
                        ErrorCodes.INVALID_ACCOUNT_TYPE,
                        "Plazo fijo requiere termDays > 0"
                ));
            }
        } else if (profile.isEnterprise()) {
            if (AccountType.SAVINGS.equals(accountType)) {
                return Single.error(new BusinessException(
                        ErrorCodes.ENTERPRISE_CANNOT_HAVE_SAVINGS,
                        "Cliente ENTERPRISE no puede tener cuenta de ahorro"
                ));
            }
            if (AccountType.TERM_FIXED.equals(accountType)) {
                return Single.error(new BusinessException(
                        ErrorCodes.ENTERPRISE_CANNOT_HAVE_TERM_FIXED,
                        "Cliente ENTERPRISE no puede tener cuenta a plazo fijo"
                ));
            }
            if (AccountType.CURRENT.equals(accountType)) {
                if (command.getHolders() == null || command.getHolders().isEmpty()) {
                    return Single.error(new BusinessException(
                            ErrorCodes.VALIDATION_ERROR,
                            "Cuenta corriente empresarial requiere al menos un titular"
                    ));
                }
                boolean hasPrincipal = command.getHolders().stream()
                        .anyMatch(h -> Boolean.TRUE.equals(h.getPrincipal()));
                if (!hasPrincipal) {
                    return Single.error(new BusinessException(
                            ErrorCodes.VALIDATION_ERROR,
                            "Cuenta corriente empresarial requiere un titular principal"
                    ));
                }
            }
        }

        return Single.just(profile);
    }

    private Single<Boolean> checkRiskView(String customerId) {
        return riskViewPort.findByCustomerId(customerId)
                .map(CustomerRiskView::getHasOverdueDebt)
                .switchIfEmpty(Single.just(false));
    }

    private Single<CustomerProfileView> checkCreditCardRequirement(
            CustomerProfileView profile,
            AccountType accountType
    ) {
        if (profile.isPersonal() && profile.isVip() && AccountType.SAVINGS.equals(accountType)) {
            return cardViewPort.findByCustomerId(profile.getCustomerId())
                    .map(view -> {
                        if (!Boolean.TRUE.equals(view.getHasCreditCard())) {
                            throw new BusinessException(
                                    ErrorCodes.CREDIT_CARD_REQUIRED_FOR_VIP,
                                    "Perfil VIP requiere tarjeta de crédito activa para abrir cuenta de ahorro"
                            );
                        }
                        return profile;
                    })
                    .switchIfEmpty(Single.just(profile));
        }

        if (profile.isEnterprise() && profile.isPyme() && AccountType.CURRENT.equals(accountType)) {
            return cardViewPort.findByCustomerId(profile.getCustomerId())
                    .map(view -> {
                        if (!Boolean.TRUE.equals(view.getHasCreditCard())) {
                            throw new BusinessException(
                                    ErrorCodes.CREDIT_CARD_REQUIRED_FOR_PYME,
                                    "Perfil PYME requiere tarjeta de crédito activa para abrir cuenta corriente"
                            );
                        }
                        return profile;
                    })
                    .switchIfEmpty(Single.just(profile));
        }

        return Single.just(profile);
    }

    private Single<CustomerProfileView> checkAccountLimits(CustomerProfileView profile, AccountType accountType) {
        if (AccountType.SAVINGS.equals(accountType)) {
            return accountPersistencePort.existsByCustomerIdAndType(profile.getCustomerId(), AccountType.SAVINGS)
                    .map(exists -> {
                        if (exists) {
                            throw new BusinessException(
                                    ErrorCodes.SAVINGS_ACCOUNT_ALREADY_EXISTS,
                                    "Cliente ya tiene cuenta de ahorro"
                            );
                        }
                        return profile;
                    });
        }

        if (AccountType.CURRENT.equals(accountType)) {
            return accountPersistencePort.countByCustomerIdAndType(profile.getCustomerId(), AccountType.CURRENT)
                    .map(count -> {
                        if (profile.isPersonal() && count > 0) {
                            throw new BusinessException(
                                    ErrorCodes.CURRENT_ACCOUNT_PERSONAL_LIMIT,
                                    "Cliente PERSONAL ya tiene cuenta corriente"
                            );
                        }
                        return profile;
                    });
        }

        return Single.just(profile);
    }

    private Single<AccountResult> createAndSaveAccount(
            OpenAccountCommand command,
            AccountType accountType,
            CustomerProfileView profile
    ) {
        Account account = buildAccount(command, accountType, profile);
        return accountPersistencePort.save(account)
                .flatMap(saved -> {
                    if (command.getInitialAmount().compareTo(BigDecimal.ZERO) > 0) {
                        Movement openingMovement = buildMovement(
                                saved.getId(),
                                saved.getCustomerId(),
                                command.getInitialAmount(),
                                MovementType.OPENING_DEPOSIT,
                                "Depósito inicial apertura cuenta",
                                command.getChannel(),
                                command.getReferenceId(),
                                BigDecimal.ZERO
                        );
                        return movementPersistencePort.save(openingMovement)
                                .map(m -> toAccountResult(saved));
                    }
                    return Single.just(toAccountResult(saved));
                });
    }

    private Account buildAccount(OpenAccountCommand command, AccountType accountType, CustomerProfileView profile) {
        LocalDateTime now = LocalDateTime.now();
        String accountNumber = generateAccountNumber();

        Account.AccountBuilder builder = Account.builder()
                .customerId(command.getCustomerId())
                .accountNumber(accountNumber)
                .type(accountType)
                .status(AccountStatus.OPEN)
                .currency(command.getCurrency() != null ? command.getCurrency() : "PEN")
                .balance(command.getInitialAmount())
                .openedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .monthlyMovementCount(0)
                .currentMonth(now.format(MONTH_FORMATTER));

        switch (accountType) {
            case SAVINGS -> builder.minOpeningAmount(BigDecimal.ZERO)
                    .maxFreeMovementsPerMonth(10)
                    .commissionPerExcessMovement(new BigDecimal("2.50"))
                    .maintenanceCommission(BigDecimal.ZERO);
            case CURRENT -> {
                builder.minOpeningAmount(BigDecimal.ZERO)
                        .maxFreeMovementsPerMonth(null)
                        .commissionPerExcessMovement(BigDecimal.ZERO)
                        .maintenanceCommission(new BigDecimal("15.00"));
                if (command.getHolders() != null) {
                    List<AccountHolder> holders = command.getHolders().stream()
                            .map(h -> AccountHolder.builder()
                                    .holderId(h.getHolderId())
                                    .percentage(h.getPercentage())
                                    .principal(h.getPrincipal() != null && h.getPrincipal())
                                    .build())
                            .toList();
                    builder.holders(holders);
                }
                if (command.getSigners() != null) {
                    List<AccountSigner> signers = command.getSigners().stream()
                            .map(s -> AccountSigner.builder()
                                    .signerId(s.getSignerId())
                                    .operationLimit(s.getOperationLimit())
                                    .active(s.getActive() != null ? s.getActive() : true)
                                    .build())
                            .toList();
                    builder.signers(signers);
                }
            }
            case TERM_FIXED -> builder.minOpeningAmount(BigDecimal.ZERO)
                    .maxFreeMovementsPerMonth(1)
                    .commissionPerExcessMovement(new BigDecimal("10.00"))
                    .maintenanceCommission(BigDecimal.ZERO)
                    .maturityDate(now.plusDays(command.getTermDays() != null ? command.getTermDays() : 30))
                    .allowedMovementDay(command.getAllowedMovementDay() != null
                            ? command.getAllowedMovementDay()
                            : now.getDayOfMonth());
            default -> {
            }
        }

        return builder.build();
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private Single<MovementResult> processMovement(
            String accountId,
            BigDecimal amount,
            MovementType type,
            String description,
            String channel,
            String referenceId,
            BigDecimal commission
    ) {
        log.debug("Procesando movimiento: accountId={}, amount={}, type={}, channel={}",
                accountId, amount, type, channel);
        return accountPersistencePort.findById(accountId)
                .switchIfEmpty(Single.error(() -> {
                    log.warn("Cuenta no encontrada para movimiento: {}", accountId);
                    return new BusinessException(
                            ErrorCodes.ACCOUNT_NOT_FOUND,
                            "Cuenta no encontrada: " + accountId
                    );
                }))
                .flatMap(account -> {
                    log.debug("Cuenta encontrada para movimiento: id={}, tipo={}, saldo={}",
                            accountId, account.getType(), account.getBalance());
                    return profileViewPort.findByCustomerId(account.getCustomerId())
                            .switchIfEmpty(Single.error(() -> {
                                log.warn("Cliente no encontrado para movimiento: customerId={}", account.getCustomerId());
                                return new BusinessException(
                                        ErrorCodes.CUSTOMER_NOT_FOUND,
                                        "Cliente no encontrado: " + account.getCustomerId()
                                );
                            }))
                            .flatMap(profile -> {
                                if (!profile.canOperate()) {
                                    log.warn("Cliente inactivo para movimiento: customerId={}", account.getCustomerId());
                                    return Single.error(new BusinessException(
                                            ErrorCodes.CUSTOMER_INACTIVE,
                                            "Cliente no está activo"
                                    ));
                                }
                                return Single.just(account);
                            });
                })
                .flatMap(account -> {
                    try {
                        validateAccountForMovement(account, amount, type);
                        log.debug("Validación de cuenta OK para movimiento: accountId={}, type={}", accountId, type);

                        BigDecimal appliedCommission = calculateCommission(account, type, commission);
                        if (appliedCommission.compareTo(BigDecimal.ZERO) > 0) {
                            log.info("Comisión aplicada en movimiento: accountId={}, type={}, commission={}",
                                    accountId, type, appliedCommission);
                        }

                        BigDecimal newBalance = account.getBalance().add(amount).subtract(appliedCommission);
                        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                            log.warn("Saldo insuficiente para movimiento: accountId={}, amount={}, balance={}, newBalance={}",
                                    accountId, amount, account.getBalance(), newBalance);
                            return Single.error(new BusinessException(
                                    ErrorCodes.ACCOUNT_INSUFFICIENT_BALANCE,
                                    "Saldo insuficiente"
                            ));
                        }

                        account.setBalance(newBalance);
                        account.incrementMovementCount(LocalDateTime.now());
                        account.setUpdatedAt(LocalDateTime.now());

                        Movement movement = buildMovement(
                                accountId,
                                account.getCustomerId(),
                                amount,
                                type,
                                description,
                                channel,
                                referenceId,
                                appliedCommission
                        );
                        movement.setBalanceAfter(newBalance);

                        return accountPersistencePort.save(account)
                                .flatMap(saved -> movementPersistencePort.save(movement))
                                .flatMap(m -> {
                                    cachePort.invalidateBalance(accountId);
                                    log.debug("Movimiento guardado: movementId={}, accountId={}, type={}, amount={}, balanceAfter={}",
                                            m.getId(), accountId, type, amount, newBalance);
                                    return publishMovementEvent(
                                            buildMovementEvent(
                                                    movement,
                                                    accountId,
                                                    account.getCustomerId(),
                                                    type,
                                                    amount,
                                                    appliedCommission
                                            )
                                    ).andThen(Single.just(m));
                                })
                                .map(m -> toMovementResult(m, newBalance));
                    } catch (BusinessException e) {
                        log.error("Error de negocio en movimiento: accountId={}, type={} - {}",
                                accountId, type, e.getMessage());
                        return Single.error(e);
                    } catch (Exception e) {
                        log.error("Error inesperado en movimiento: accountId={}, type={} - {}",
                                accountId, type, e.getMessage(), e);
                        return Single.error(e);
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private void validateAccountForMovement(Account account, BigDecimal amount, MovementType type) {
        if (!account.canOperate()) {
            log.warn("Cuenta no operativa para movimiento: id={}, estado={}", account.getId(), account.getStatus());
            throw new BusinessException(ErrorCodes.ACCOUNT_BLOCKED, "Cuenta no operativa (cerrada o bloqueada)");
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Monto inválido para movimiento: accountId={}, amount={}", account.getId(), amount);
            throw new BusinessException(ErrorCodes.INVALID_MOVEMENT_AMOUNT, "Monto debe ser mayor a 0");
        }

        if (AccountType.TERM_FIXED.equals(account.getType())) {
            validateTermFixedMovement(account);
        }

        if (AccountType.SAVINGS.equals(account.getType())
                && type != MovementType.COMMISSION
                && type != MovementType.INTEREST
                && account.hasExceededFreeMovements()) {
            log.warn("Límite de movimientos excedido: accountId={}, count={}, limit={}",
                    account.getId(), account.getMonthlyMovementCount(), account.getMaxFreeMovementsPerMonth());
            throw new BusinessException(
                    ErrorCodes.EXCEEDED_FREE_MOVEMENTS,
                    "Excede movimientos gratuitos mensuales (límite: " + account.getMaxFreeMovementsPerMonth() + ")"
            );
        }
    }

    private void validateTermFixedMovement(Account account) {
        LocalDateTime now = LocalDateTime.now();
        int today = now.getDayOfMonth();
        int allowedDay = account.getAllowedMovementDay() != null ? account.getAllowedMovementDay() : 1;

        log.debug("Validando movimiento plazo fijo: accountId={}, today={}, allowedDay={}",
                account.getId(), today, allowedDay);

        if (today != allowedDay) {
            log.warn("Movimiento rechazado en plazo fijo: accountId={}, today={}, allowedDay={}",
                    account.getId(), today, allowedDay);
            throw new BusinessException(
                    ErrorCodes.MOVEMENT_DAY_NOT_ALLOWED,
                    "Plazo fijo solo permite movimientos el día " + allowedDay + " de cada mes"
            );
        }

        String currentMonth = now.format(MONTH_FORMATTER);
        if (currentMonth.equals(account.getCurrentMonth())
                && account.getMonthlyMovementCount() != null
                && account.getMonthlyMovementCount() > 0) {
            log.warn("Movimiento duplicado en plazo fijo: accountId={}, month={}, count={}",
                    account.getId(), currentMonth, account.getMonthlyMovementCount());
            throw new BusinessException(
                    ErrorCodes.MOVEMENT_ALREADY_DONE_TODAY,
                    "Ya realizó un movimiento hoy (límite 1 por día permitido)"
            );
        }
    }

    private BigDecimal calculateCommission(Account account, MovementType type, BigDecimal baseCommission) {
        if (AccountType.SAVINGS.equals(account.getType())
                && type != MovementType.COMMISSION
                && type != MovementType.INTEREST
                && account.hasExceededFreeMovements()) {
            BigDecimal commission = account.getCommissionPerExcessMovement() != null
                    ? account.getCommissionPerExcessMovement()
                    : BigDecimal.ZERO;
            log.debug("Comisión por exceso de movimientos: accountId={}, commission={}", account.getId(), commission);
            return commission;
        }
        BigDecimal result = baseCommission != null ? baseCommission : BigDecimal.ZERO;
        if (result.compareTo(BigDecimal.ZERO) > 0) {
            log.debug("Comisión base aplicada: accountId={}, commission={}", account.getId(), result);
        }
        return result;
    }

    private Movement buildMovement(
            String accountId,
            String customerId,
            BigDecimal amount,
            MovementType type,
            String description,
            String channel,
            String referenceId,
            BigDecimal commission
    ) {
        return Movement.builder()
                .id(UUID.randomUUID().toString())
                .accountId(accountId)
                .customerId(customerId)
                .type(type)
                .amount(amount)
                .commission(commission)
                .channel(channel != null ? channel : "AUTO")
                .description(description)
                .referenceId(referenceId)
                .occurredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void invalidateCache(String accountId) {
        cachePort.invalidateBalance(accountId);
    }

    private Single<List<Movement>> validateAndDebitAccount(String accountId, BigDecimal amount, String referenceId) {
        log.debug("Validando y debitando cuenta: accountId={}, amount={}, referenceId={}",
                accountId, amount, referenceId);
        return accountPersistencePort.findById(accountId)
                .switchIfEmpty(Single.error(() -> {
                    log.warn("Cuenta no encontrada para débito: {}", accountId);
                    return new BusinessException(
                            ErrorCodes.ACCOUNT_NOT_FOUND,
                            "Cuenta no encontrada: " + accountId
                    );
                }))
                .flatMap(account -> {
                    try {
                        validateAccountForMovement(account, amount.negate(), MovementType.DEBIT_PAYMENT);

                        BigDecimal newBalance = account.getBalance().subtract(amount);
                        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                            log.warn("Saldo insuficiente para pago débito: accountId={}, amount={}, balance={}",
                                    accountId, amount, account.getBalance());
                            return Single.error(new BusinessException(
                                    ErrorCodes.ACCOUNT_INSUFFICIENT_BALANCE,
                                    "Saldo insuficiente para pago débito"
                            ));
                        }

                        account.setBalance(newBalance);
                        account.setUpdatedAt(LocalDateTime.now());

                        Movement movement = buildMovement(
                                accountId,
                                account.getCustomerId(),
                                amount.negate(),
                                MovementType.DEBIT_PAYMENT,
                                "Pago con tarjeta débito",
                                "AUTO",
                                referenceId,
                                BigDecimal.ZERO
                        );
                        movement.setBalanceAfter(newBalance);

                        return accountPersistencePort.save(account)
                                .flatMap(saved -> {
                                    invalidateCache(accountId);
                                    return movementPersistencePort.save(movement);
                                })
                                .map(m -> {
                                    log.info("Débito aplicado: accountId={}, amount={}, newBalance={}",
                                            accountId, amount, newBalance);
                                    return List.of(m);
                                });
                    } catch (Exception e) {
                        log.error("Error validando débito: accountId={} - {}", accountId, e.getMessage(), e);
                        return Single.error(e);
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private Single<List<Movement>> validateAndTransfer(
            String sourceAccountId,
            String targetAccountId,
            BigDecimal amount,
            String transferId,
            String description
    ) {
        log.debug("Validando transferencia: source={}, target={}, amount={}, transferId={}",
                sourceAccountId, targetAccountId, amount, transferId);
        return accountPersistencePort.findById(sourceAccountId)
                .switchIfEmpty(Single.error(() -> {
                    log.warn("Cuenta origen no encontrada para transferencia: {}", sourceAccountId);
                    return new BusinessException(
                            ErrorCodes.ACCOUNT_NOT_FOUND,
                            "Cuenta origen no encontrada"
                    );
                }))
                .flatMap(source -> {
                    log.debug("Cuenta origen encontrada: id={}, balance={}", sourceAccountId, source.getBalance());
                    return accountPersistencePort.findById(targetAccountId)
                            .switchIfEmpty(Single.error(() -> {
                                log.warn("Cuenta destino no encontrada para transferencia: {}", targetAccountId);
                                return new BusinessException(
                                        ErrorCodes.ACCOUNT_NOT_FOUND,
                                        "Cuenta destino no encontrada"
                                );
                            }))
                            .flatMap(target -> {
                                try {
                                    validateAccountForMovement(source, amount.negate(), MovementType.TRANSFER_OUT);
                                    validateAccountForMovement(target, amount, MovementType.TRANSFER_IN);

                                    BigDecimal sourceNewBalance = source.getBalance().subtract(amount);
                                    if (sourceNewBalance.compareTo(BigDecimal.ZERO) < 0) {
                                        log.warn("Saldo insuficiente en cuenta origen: source={}, amount={}, balance={}",
                                                sourceAccountId, amount, source.getBalance());
                                        return Single.error(new BusinessException(
                                                ErrorCodes.ACCOUNT_INSUFFICIENT_BALANCE,
                                                "Saldo insuficiente en cuenta origen"
                                        ));
                                    }

                                    source.setBalance(sourceNewBalance);
                                    source.setUpdatedAt(LocalDateTime.now());

                                    target.setBalance(target.getBalance().add(amount));
                                    target.setUpdatedAt(LocalDateTime.now());

                                    Movement outMovement = buildMovement(
                                            sourceAccountId,
                                            source.getCustomerId(),
                                            amount.negate(),
                                            MovementType.TRANSFER_OUT,
                                            description,
                                            "AUTO",
                                            transferId,
                                            BigDecimal.ZERO
                                    );
                                    outMovement.setBalanceAfter(sourceNewBalance);

                                    Movement inMovement = buildMovement(
                                            targetAccountId,
                                            target.getCustomerId(),
                                            amount,
                                            MovementType.TRANSFER_IN,
                                            description,
                                            "AUTO",
                                            transferId,
                                            BigDecimal.ZERO
                                    );
                                    inMovement.setBalanceAfter(target.getBalance());

                                    return accountPersistencePort.save(source)
                                            .flatMap(s -> accountPersistencePort.save(target))
                                            .flatMap(t -> movementPersistencePort.save(outMovement))
                                            .flatMap(out -> {
                                                invalidateCache(sourceAccountId);
                                                invalidateCache(targetAccountId);
                                                return movementPersistencePort.save(inMovement)
                                                        .map(in -> {
                                                            log.info("Transferencia completada: source={}, target={}, amount={}",
                                                                    sourceAccountId, targetAccountId, amount);
                                                            return List.of(out, in);
                                                        });
                                            });
                                } catch (Exception e) {
                                    log.error("Error validando transferencia: source={}, target={} - {}",
                                            sourceAccountId, targetAccountId, e.getMessage(), e);
                                    return Single.error(e);
                                }
                            });
                })
                .subscribeOn(Schedulers.io());
    }

    private Completable validateRiskForTransfer(String sourceCustomerId, String targetCustomerId) {
        return riskViewPort.findByCustomerId(sourceCustomerId)
                .toSingle()
                .flatMapCompletable(view -> {
                    if (Boolean.TRUE.equals(view.getHasOverdueDebt())) {
                        return Completable.error(new BusinessException(
                                ErrorCodes.CUSTOMER_HAS_OVERDUE_DEBT,
                                "Cliente origen tiene deuda vencida"
                        ));
                    }
                    return Completable.complete();
                })
                .onErrorResumeNext(throwable -> Completable.complete());
    }

    private String buildIdempotencyKey(String topic, String entityId, LocalDateTime occurredAt) {
        String occurredAtStr = occurredAt != null ? occurredAt.toString() : "no-date";
        return topic + ":" + entityId + ":" + occurredAtStr;
    }

    private Completable publishAccountOpenedEvent(AccountResult result) {
        log.debug("Publicando evento de cuenta abierta: accountId={}", result.getAccountId());
        try {
            AccountOpenedEvent event = AccountOpenedEvent.builder()
                    .accountId(result.getAccountId())
                    .customerId(result.getCustomerId())
                    .productType(result.getAccountType())
                    .balance(result.getBalance())
                    .maintenanceFee(result.getMaintenanceCommission() != null
                            ? result.getMaintenanceCommission()
                            : BigDecimal.ZERO)
                    .occurredAt(LocalDateTime.now())
                    .build();
            return eventPublisherPort.publishAccountOpened(event)
                    .doOnComplete(() -> log.debug("Evento de cuenta abierta publicado: accountId={}", result.getAccountId()))
                    .doOnError(throwable -> log.error("Error publicando evento de cuenta abierta: accountId={} - {}",
                            result.getAccountId(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado publicando evento de cuenta abierta: accountId={} - {}",
                    result.getAccountId(), e.getMessage(), e);
            return Completable.error(e);
        }
    }

    private Completable publishAccountClosedEvent(String accountId) {
        log.debug("Publicando evento de cuenta cerrada: accountId={}", accountId);
        try {
            AccountClosedEvent event = AccountClosedEvent.builder()
                    .accountId(accountId)
                    .occurredAt(LocalDateTime.now())
                    .build();
            return eventPublisherPort.publishAccountClosed(event)
                    .doOnComplete(() -> log.debug("Evento de cuenta cerrada publicado: accountId={}", accountId))
                    .doOnError(throwable -> log.error("Error publicando evento de cuenta cerrada: accountId={} - {}",
                            accountId, throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado publicando evento de cuenta cerrada: accountId={} - {}",
                    accountId, e.getMessage(), e);
            return Completable.error(e);
        }
    }

    private AccountMovementRegisteredEvent buildMovementEvent(
            Movement movement,
            String accountId,
            String customerId,
            MovementType type,
            BigDecimal amount,
            BigDecimal commission
    ) {
        return AccountMovementRegisteredEvent.builder()
                .movementId(movement.getId())
                .accountId(accountId)
                .customerId(customerId)
                .movementType(type.name())
                .amount(amount.abs())
                .balanceAfter(movement.getBalanceAfter())
                .commission(commission)
                .occurredAt(movement.getOccurredAt())
                .build();
    }

    private Completable publishMovementEvent(AccountMovementRegisteredEvent event) {
        log.debug("Publicando evento de movimiento: movementId={}, accountId={}, type={}",
                event.getMovementId(), event.getAccountId(), event.getMovementType());
        return eventPublisherPort.publishMovementRegistered(event)
                .doOnComplete(() -> log.debug("Evento de movimiento publicado: movementId={}", event.getMovementId()))
                .doOnError(throwable -> log.error("Error publicando evento de movimiento: movementId={} - {}",
                        event.getMovementId(), throwable.getMessage()));
    }

    private Completable publishDebitPaymentSettledEvent(
            String paymentId,
            String cardId,
            String status,
            String reason
    ) {
        log.debug("Publicando evento de pago débito: paymentId={}, status={}", paymentId, status);
        try {
            DebitPaymentSettledEvent event = DebitPaymentSettledEvent.builder()
                    .paymentId(paymentId)
                    .cardId(cardId)
                    .status(status)
                    .reason(reason)
                    .occurredAt(LocalDateTime.now())
                    .build();
            return eventPublisherPort.publishDebitPaymentSettled(event)
                    .doOnComplete(() -> log.debug("Evento de pago débito publicado: paymentId={}, status={}", paymentId, status))
                    .doOnError(throwable -> log.error("Error publicando evento de pago débito: paymentId={} - {}",
                            paymentId, throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado publicando evento de pago débito: paymentId={} - {}",
                    paymentId, e.getMessage(), e);
            return Completable.error(e);
        }
    }

    private Completable publishTransferSettledEvent(String transferId, String status, String reason) {
        log.debug("Publicando evento de transferencia: transferId={}, status={}", transferId, status);
        try {
            TransferSettledEvent event = TransferSettledEvent.builder()
                    .transferId(transferId)
                    .status(status)
                    .reason(reason)
                    .occurredAt(LocalDateTime.now())
                    .build();
            return eventPublisherPort.publishTransferSettled(event)
                    .doOnComplete(() -> log.debug("Evento de transferencia publicado: transferId={}, status={}", transferId, status))
                    .doOnError(throwable -> log.error("Error publicando evento de transferencia: transferId={} - {}",
                            transferId, throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado publicando evento de transferencia: transferId={} - {}",
                    transferId, e.getMessage(), e);
            return Completable.error(e);
        }
    }

    private AccountResult toAccountResult(Account account) {
        return AccountResult.builder()
                .accountId(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getType().name())
                .status(account.getStatus().name())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .openedAt(account.getOpenedAt())
                .customerId(account.getCustomerId())
                .maintenanceCommission(account.getMaintenanceCommission())
                .holders(account.getHolders() != null
                        ? account.getHolders().stream()
                        .map(h -> AccountResult.AccountHolderInfo.builder()
                                .holderId(h.getHolderId())
                                .percentage(h.getPercentage())
                                .principal(h.getPrincipal())
                                .build())
                        .toList()
                        : null)
                .signers(account.getSigners() != null
                        ? account.getSigners().stream()
                        .map(s -> AccountResult.AccountSignerInfo.builder()
                                .signerId(s.getSignerId())
                                .operationLimit(s.getOperationLimit())
                                .active(s.getActive())
                                .build())
                        .toList()
                        : null)
                .build();
    }

    private MovementResult toMovementResult(Movement movement, BigDecimal balanceAfter) {
        return MovementResult.builder()
                .movementId(movement.getId())
                .accountId(movement.getAccountId())
                .movementType(movement.getType().name())
                .amount(movement.getAmount())
                .balanceAfter(balanceAfter)
                .commission(movement.getCommission())
                .description(movement.getDescription())
                .occurredAt(movement.getOccurredAt())
                .build();
    }

    private AccountType parseAccountType(String type) {
        try {
            return AccountType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCodes.INVALID_ACCOUNT_TYPE, "Tipo de cuenta inválido: " + type);
        }
    }

    // ==================== Convenience methods for controllers and consumers ====================

    /**
     * Alias para abrir cuenta (usado por el controller REST).
     */
    public Single<AccountResult> openAccount(OpenAccountCommand command) {
        return execute(command);
    }

    /**
     * Alias para depósito (usado por el controller REST).
     */
    public Single<MovementResult> deposit(DepositCommand command) {
        return execute(command);
    }

    /**
     * Alias para retiro (usado por el controller REST).
     */
    public Single<MovementResult> withdrawal(WithdrawalCommand command) {
        return execute(command);
    }

    /**
     * Alias para cerrar cuenta (usado por el controller REST).
     */
    public Completable closeAccount(String accountId) {
        return execute(accountId);
    }

    /**
     * Alias para liquidar pago débito (usado por el consumidor Kafka).
     */
    public Completable settleDebitPayment(DebitPaymentCommand command) {
        return execute(command);
    }
}
