package com.yanki.accounts.domain.service;

import com.yanki.accounts.domain.event.MaintenanceAppliedEvent;
import com.yanki.accounts.domain.event.MaintenanceFailedEvent;
import com.yanki.accounts.domain.event.VipAverageBreachedEvent;
import com.yanki.accounts.domain.exception.BusinessException;
import com.yanki.accounts.domain.exception.ErrorCodes;
import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.port.out.CachePort;
import com.yanki.accounts.domain.port.out.EventPublisherPort;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Servicio de jobs programados para la gestión de cuentas bancarias.
 *
 * <p>Ejecuta tareas periódicas como la aplicación de comisiones de mantenimiento,
 * la verificación de promedios VIP y la limpieza de contadores de movimientos.</p>
 *
 * <p>Jobs programados:</p>
 * <ul>
 *   <li>{@code vip-daily-average-check}: Día 1, 02:00 — Calcula promedio diario VIP</li>
 *   <li>{@code maintenance-commission}: Día 1, 03:00 — Aplica comisión mantenimiento mensual</li>
 *   <li>{@code cleanup-movement-counters}: Día 1, 04:00 — Limpia contadores del mes anterior</li>
 * </ul>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSchedulerService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final AccountPersistencePort accountPersistencePort;
    private final MovementPersistencePort movementPersistencePort;
    private final EventPublisherPort eventPublisherPort;
    private final CachePort cachePort;

    /**
     * Job programado que verifica el promedio diario de las cuentas VIP.
     * Se ejecuta el día 1 de cada mes a las 02:00.
     *
     * <p>Calcula el promedio diario del mes anterior para cada cuenta VIP.
     * Si el promedio está por debajo del mínimo requerido (5000.00), aplica
     * una penalidad (50.00) y publica el evento vip.average.breached.v1.</p>
     */
    @Scheduled(cron = "${yanki.accounts.jobs.vip-daily-average-check:0 0 2 1 * ?}")
    public void checkVipDailyAverage() {
        log.info("Iniciando job de verificación de promedio diario VIP");
        try {
            accountPersistencePort.findAllByTypeAndStatus(AccountType.SAVINGS, AccountStatus.OPEN)
                    .toList()
                    .toFlowable()
                    .flatMapCompletable(accounts -> {
                        log.info("Cuentas SAVINGS abiertas encontradas para verificación VIP: {}", accounts.size());
                        return Flowable.fromIterable(accounts)
                                .concatMapCompletable(this::processVipAccount);
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            () -> log.info("Job de verificación de promedio VIP completado exitosamente"),
                            error -> log.error("Error en job de verificación de promedio VIP: {}", error.getMessage(), error)
                    );
        } catch (Exception e) {
            log.error("Error inesperado en job de verificación de promedio VIP: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa una cuenta individual para verificar su promedio VIP.
     */
    private Completable processVipAccount(Account account) {
        String previousMonth = LocalDate.now().minusMonths(1).format(MONTH_FORMATTER);
        log.debug("Procesando cuenta VIP: accountId={}, month={}", account.getId(), previousMonth);

        try {
            return movementPersistencePort.findByAccountIdAndMonth(account.getId(), previousMonth)
                    .toList()
                    .toFlowable()
                    .flatMapCompletable(movements -> {
                        if (movements.isEmpty()) {
                            log.debug("No hay movimientos para cuenta VIP: accountId={}, month={}", account.getId(), previousMonth);
                            return Completable.complete();
                        }

                        log.debug("Movimientos encontrados para cuenta VIP: accountId={}, count={}", account.getId(), movements.size());
                        BigDecimal totalBalance = movements.stream()
                                .map(Movement::getBalanceAfter)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                        int daysInMonth = LocalDate.now().minusMonths(1).lengthOfMonth();
                        BigDecimal dailyAverage = totalBalance.divide(
                                BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);

                        BigDecimal minRequiredAverage = new BigDecimal("5000.00");
                        BigDecimal penaltyAmount = new BigDecimal("50.00");

                        if (dailyAverage.compareTo(minRequiredAverage) < 0) {
                            log.warn("Cuenta {} con promedio VIP por debajo del mínimo: {} < {}",
                                    account.getId(), dailyAverage, minRequiredAverage);
                            return applyVipPenalty(account, dailyAverage, minRequiredAverage, penaltyAmount, previousMonth);
                        } else {
                            log.debug("Cuenta VIP OK: accountId={}, dailyAverage={}, minRequired={}",
                                    account.getId(), dailyAverage, minRequiredAverage);
                        }
                        return Completable.complete();
                    });
        } catch (Exception e) {
            log.error("Error procesando cuenta VIP: accountId={} - {}", account.getId(), e.getMessage(), e);
            return Completable.error(e);
        }
    }

    /**
     * Aplica la penalidad por incumplimiento de promedio VIP.
     */
    private Completable applyVipPenalty(Account account, BigDecimal dailyAverage,
            BigDecimal minRequiredAverage, BigDecimal penaltyAmount, String month) {
        log.info("Aplicando penalidad VIP: accountId={}, dailyAverage={}, penalty={}",
                account.getId(), dailyAverage, penaltyAmount);
        BigDecimal newBalance = account.getBalance().subtract(penaltyAmount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Saldo insuficiente para aplicar penalidad VIP en cuenta {}: balance={}, penalty={}",
                    account.getId(), account.getBalance(), penaltyAmount);
            return Completable.complete();
        }

        try {
            account.setBalance(newBalance);
            account.setUpdatedAt(LocalDateTime.now());

            Movement penaltyMovement = Movement.builder()
                    .accountId(account.getId())
                    .customerId(account.getCustomerId())
                    .type(MovementType.COMMISSION)
                    .amount(penaltyAmount.negate())
                    .balanceAfter(newBalance)
                    .commission(penaltyAmount)
                    .channel("AUTO")
                    .description("Penalidad por promedio diario VIP por debajo del mínimo")
                    .occurredAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            return accountPersistencePort.save(account)
                    .ignoreElement()
                    .andThen(movementPersistencePort.save(penaltyMovement).ignoreElement())
                    .andThen(Completable.fromAction(() -> {
                        cachePort.invalidateBalance(account.getId());
                        log.info("Penalidad VIP aplicada: accountId={}, newBalance={}", account.getId(), newBalance);
                    }))
                    .andThen(publishVipAverageBreached(account, dailyAverage, minRequiredAverage, penaltyAmount, newBalance, month))
                    .doOnError(throwable -> log.error("Error aplicando penalidad VIP: accountId={} - {}",
                            account.getId(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado aplicando penalidad VIP: accountId={} - {}", account.getId(), e.getMessage(), e);
            return Completable.error(e);
        }
    }

    /**
     * Publica el evento de promedio VIP por debajo del mínimo.
     */
    private Completable publishVipAverageBreached(Account account, BigDecimal dailyAverage,
            BigDecimal minRequiredAverage, BigDecimal penaltyAmount, BigDecimal balanceAfter, String month) {
        VipAverageBreachedEvent event = VipAverageBreachedEvent.builder()
                .accountId(account.getId())
                .customerId(account.getCustomerId())
                .dailyAverage(dailyAverage)
                .minRequiredAverage(minRequiredAverage)
                .penaltyAmount(penaltyAmount)
                .balanceAfter(balanceAfter)
                .month(month)
                .occurredAt(LocalDateTime.now())
                .build();
        return eventPublisherPort.publishVipAverageBreached(event);
    }

    /**
     * Job programado que aplica la comisión de mantenimiento mensual.
     * Se ejecuta el día 1 de cada mes a las 03:00.
     *
     * <p>Aplica la comisión de mantenimiento a todas las cuentas corriente
     * con maintenanceCommission > 0. Si el saldo es insuficiente, publica
     * el evento maintenance.failed.v1.</p>
     */
    @Scheduled(cron = "${yanki.accounts.jobs.maintenance-commission:0 0 3 1 * ?}")
    public void applyMaintenanceCommission() {
        log.info("Iniciando job de aplicación de comisiones de mantenimiento");
        try {
            accountPersistencePort.findAllByTypeAndStatus(AccountType.CURRENT, AccountStatus.OPEN)
                    .toList()
                    .toFlowable()
                    .flatMapCompletable(accounts -> {
                        log.info("Cuentas CURRENT abiertas encontradas para comisión de mantenimiento: {}", accounts.size());
                        return Flowable.fromIterable(accounts)
                                .concatMapCompletable(this::processMaintenanceCommission);
                    })
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            () -> log.info("Job de comisiones de mantenimiento completado exitosamente"),
                            error -> log.error("Error en job de comisiones de mantenimiento: {}", error.getMessage(), error)
                    );
        } catch (Exception e) {
            log.error("Error inesperado en job de comisiones de mantenimiento: {}", e.getMessage(), e);
        }
    }

    /**
     * Procesa la comisión de mantenimiento de una cuenta individual.
     */
    private Completable processMaintenanceCommission(Account account) {
        BigDecimal maintenanceCommission = account.getMaintenanceCommission();
        log.debug("Procesando comisión de mantenimiento: accountId={}, commission={}",
                account.getId(), maintenanceCommission);

        if (maintenanceCommission == null || maintenanceCommission.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("Sin comisión de mantenimiento: accountId={}", account.getId());
            return Completable.complete();
        }

        String previousMonth = LocalDate.now().minusMonths(1).format(MONTH_FORMATTER);

        if (account.getBalance().compareTo(maintenanceCommission) < 0) {
            log.warn("Saldo insuficiente para comisión de mantenimiento en cuenta {}: balance={}, commission={}",
                    account.getId(), account.getBalance(), maintenanceCommission);
            return publishMaintenanceFailed(account, maintenanceCommission, previousMonth, "SALDO_INSUFICIENTE");
        }

        try {
            BigDecimal newBalance = account.getBalance().subtract(maintenanceCommission);
            account.setBalance(newBalance);
            account.setUpdatedAt(LocalDateTime.now());

            Movement commissionMovement = Movement.builder()
                    .accountId(account.getId())
                    .customerId(account.getCustomerId())
                    .type(MovementType.COMMISSION)
                    .amount(maintenanceCommission.negate())
                    .balanceAfter(newBalance)
                    .commission(maintenanceCommission)
                    .channel("AUTO")
                    .description("Comisión de mantenimiento mensual")
                    .occurredAt(LocalDateTime.now())
                    .createdAt(LocalDateTime.now())
                    .build();

            return accountPersistencePort.save(account)
                    .ignoreElement()
                    .andThen(movementPersistencePort.save(commissionMovement).ignoreElement())
                    .andThen(Completable.fromAction(() -> {
                        cachePort.invalidateBalance(account.getId());
                        log.info("Comisión de mantenimiento aplicada: accountId={}, commission={}, newBalance={}",
                                account.getId(), maintenanceCommission, newBalance);
                    }))
                    .andThen(publishMaintenanceApplied(account, maintenanceCommission, newBalance, previousMonth))
                    .doOnError(throwable -> log.error("Error aplicando comisión de mantenimiento: accountId={} - {}",
                            account.getId(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("Error inesperado aplicando comisión de mantenimiento: accountId={} - {}",
                    account.getId(), e.getMessage(), e);
            return Completable.error(e);
        }
    }

    /**
     * Publica el evento de comisión de mantenimiento aplicada.
     */
    private Completable publishMaintenanceApplied(Account account, BigDecimal commissionAmount,
            BigDecimal balanceAfter, String month) {
        MaintenanceAppliedEvent event = MaintenanceAppliedEvent.builder()
                .accountId(account.getId())
                .customerId(account.getCustomerId())
                .commissionAmount(commissionAmount)
                .balanceAfter(balanceAfter)
                .month(month)
                .occurredAt(LocalDateTime.now())
                .build();
        return eventPublisherPort.publishMaintenanceApplied(event);
    }

    /**
     * Publica el evento de comisión de mantenimiento fallida.
     */
    private Completable publishMaintenanceFailed(Account account, BigDecimal commissionAmount,
            String month, String reason) {
        MaintenanceFailedEvent event = MaintenanceFailedEvent.builder()
                .accountId(account.getId())
                .customerId(account.getCustomerId())
                .commissionAmount(commissionAmount)
                .currentBalance(account.getBalance())
                .month(month)
                .reason(reason)
                .occurredAt(LocalDateTime.now())
                .build();
        return eventPublisherPort.publishMaintenanceFailed(event);
    }

    /**
     * Job programado que limpia los contadores de movimientos del mes anterior.
     * Se ejecuta el día 1 de cada mes a las 04:00.
     */
    @Scheduled(cron = "${yanki.accounts.jobs.cleanup-movement-counters:0 0 4 1 * ?}")
    public void cleanupMovementCounters() {
        String previousMonth = LocalDate.now().minusMonths(1).format(MONTH_FORMATTER);
        log.info("Iniciando job de limpieza de contadores de movimientos para el mes {}", previousMonth);
        try {
            cachePort.cleanupMovementCounters(previousMonth);
            log.info("Job de limpieza de contadores de movimientos completado exitosamente para el mes {}", previousMonth);
        } catch (Exception e) {
            log.error("Error en job de limpieza de contadores de movimientos para el mes {}: {}",
                    previousMonth, e.getMessage(), e);
        }
    }

    // ==================== Métodos para trigger manual (endpoints admin) ====================

    /**
     * Ejecuta manualmente el job de verificación de promedio VIP.
     * Usado por el endpoint admin para pruebas y recuperación.
     */
    public void triggerVipAverageCheck() {
        log.info("Trigger manual: verificación de promedio VIP");
        try {
            checkVipDailyAverage();
        } catch (Exception e) {
            log.error("Error en trigger manual de verificación VIP: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta manualmente el job de comisiones de mantenimiento.
     * Usado por el endpoint admin para pruebas y recuperación.
     */
    public void triggerMaintenanceCommission() {
        log.info("Trigger manual: aplicación de comisiones de mantenimiento");
        try {
            applyMaintenanceCommission();
        } catch (Exception e) {
            log.error("Error en trigger manual de comisiones de mantenimiento: {}", e.getMessage(), e);
        }
    }

    /**
     * Ejecuta manualmente el job de limpieza de contadores.
     * Usado por el endpoint admin para pruebas y recuperación.
     */
    public void triggerCleanupMovementCounters() {
        log.info("Trigger manual: limpieza de contadores de movimientos");
        try {
            cleanupMovementCounters();
        } catch (Exception e) {
            log.error("Error en trigger manual de limpieza de contadores: {}", e.getMessage(), e);
        }
    }

    /**
     * Obtiene el contador de movimientos actual de una cuenta para el mes en curso.
     *
     * @param accountId identificador de la cuenta
     * @return contador de movimientos del mes actual
     */
    public Single<Integer> getMovementCount(String accountId) {
        String currentMonth = LocalDate.now().format(MONTH_FORMATTER);
        log.debug("Consultando contador de movimientos: accountId={}, month={}", accountId, currentMonth);
        return cachePort.getMovementCount(accountId, currentMonth)
                .doOnSuccess(count -> log.debug("Contador de movimientos: accountId={}, count={}", accountId, count))
                .doOnError(throwable -> log.error("Error consultando contador de movimientos: accountId={} - {}",
                        accountId, throwable.getMessage()));
    }
}
