package com.yanki.accounts.infrastructure.persistence.adapter;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import com.yanki.accounts.infrastructure.persistence.mapper.MovementDocumentMapper;
import com.yanki.accounts.infrastructure.persistence.repository.SpringDataMovementRepository;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador de salida que implementa la persistencia de movimientos sobre MongoDB Reactivo.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MovementMongoAdapter implements MovementPersistencePort {

    private final SpringDataMovementRepository repository;

    @Override
    public Maybe<Movement> findById(String id) {
        log.debug("[MongoDB] Buscando movimiento por ID: {}", id);
        return Maybe.fromPublisher(
                repository.findById(id).map(MovementDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando movimiento por ID: {} - {}", id, throwable.getMessage()));
    }

    @Override
    public Single<Movement> save(Movement movement) {
        log.debug("[MongoDB] Guardando movimiento: accountId={}, type={}, amount={}",
                movement.getAccountId(), movement.getType(), movement.getAmount());
        return Single.fromPublisher(
                repository.save(MovementDocumentMapper.toDocument(movement))
                        .map(MovementDocumentMapper::toDomain)
        ).doOnSuccess(saved -> log.debug("[MongoDB] Movimiento guardado: id={}", saved.getId()))
         .doOnError(throwable -> log.error("[MongoDB] Error guardando movimiento: accountId={} - {}",
                 movement.getAccountId(), throwable.getMessage()));
    }

    @Override
    public Flowable<Movement> findAllByAccountIdOrderByOccurredAtDesc(String accountId) {
        log.debug("[MongoDB] Listando movimientos por accountId (desc): {}", accountId);
        return Flowable.fromPublisher(
                repository.findAllByAccountIdOrderByOccurredAtDesc(accountId)
                        .map(MovementDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error listando movimientos: accountId={} - {}", accountId, throwable.getMessage()));
    }

    @Override
    public Flowable<Movement> findAllByAccountIdAndOccurredAtBetween(
            String accountId, LocalDateTime from, LocalDateTime to) {
        log.debug("[MongoDB] Buscando movimientos: accountId={}, from={}, to={}", accountId, from, to);
        return Flowable.fromPublisher(
                repository.findAllByAccountIdAndOccurredAtBetween(accountId, from, to)
                        .map(MovementDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando movimientos por rango: accountId={} - {}", accountId, throwable.getMessage()));
    }

    @Override
    public Flowable<Movement> findAllByCustomerIdOrderByOccurredAtDesc(String customerId) {
        log.debug("[MongoDB] Listando movimientos por customerId (desc): {}", customerId);
        return Flowable.fromPublisher(
                repository.findAllByCustomerIdOrderByOccurredAtDesc(customerId)
                        .map(MovementDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error listando movimientos por customerId: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Single<Long> countByAccountIdAndOccurredAtBetween(
            String accountId, LocalDateTime from, LocalDateTime to) {
        log.debug("[MongoDB] Contando movimientos: accountId={}, from={}, to={}", accountId, from, to);
        return Single.fromPublisher(
                repository.countByAccountIdAndOccurredAtBetween(accountId, from, to)
        ).doOnError(throwable -> log.error("[MongoDB] Error contando movimientos: accountId={} - {}", accountId, throwable.getMessage()));
    }

    @Override
    public Maybe<Movement> findFirstByAccountIdOrderByOccurredAtDesc(String accountId) {
        log.debug("[MongoDB] Buscando último movimiento: accountId={}", accountId);
        return Maybe.fromPublisher(
                repository.findFirstByAccountIdOrderByOccurredAtDesc(accountId)
                        .map(MovementDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando último movimiento: accountId={} - {}", accountId, throwable.getMessage()));
    }

    @Override
    public Flowable<Movement> findByAccountIdAndMonth(String accountId, String month) {
        log.debug("[MongoDB] Buscando movimientos por mes: accountId={}, month={}", accountId, month);
        try {
            YearMonth yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyy-MM"));
            LocalDateTime from = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime to = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            return Flowable.fromPublisher(
                    repository.findAllByAccountIdAndOccurredAtBetween(accountId, from, to)
                            .map(MovementDocumentMapper::toDomain)
            ).doOnError(throwable -> log.error("[MongoDB] Error buscando movimientos por mes: accountId={} - {}", accountId, throwable.getMessage()));
        } catch (Exception e) {
            log.error("[MongoDB] Error parseando mes: accountId={}, month={} - {}", accountId, month, e.getMessage(), e);
            throw e;
        }
    }
}
