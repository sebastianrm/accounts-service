package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Estrategia para filtrar movimientos por rango de fechas (from y to).
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.MovementFilterCriteria}
 * contiene {@code from} y {@code to} activos, mientras {@code type} es null.
 * Delega en {@code movementPersistencePort.findAllByAccountIdAndOccurredAtBetween()} para obtener
 * los movimientos dentro del rango especificado.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts/{id}/movements?from=2024-01-01&to=2024-03-31}
 * solicitando los movimientos de un trimestre específico.</p>
 *
 * @see MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByFromAndToMovementStrategy implements MovementFilterStrategy {

    private final MovementPersistencePort movementPersistencePort;
    private final String accountId;
    private final LocalDateTime from;
    private final LocalDateTime to;

    @Override
    public Flowable<Movement> find() {
        return Flowable.defer(() ->
                movementPersistencePort.findAllByAccountIdAndOccurredAtBetween(accountId, from, to)
        ).subscribeOn(Schedulers.io());
    }
}
