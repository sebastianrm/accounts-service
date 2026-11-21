package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Estrategia para filtrar movimientos por fecha de inicio y tipo (from y type).
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.MovementFilterCriteria}
 * contiene {@code from} y {@code type} activos, mientras {@code to} es null.
 * Filtra los movimientos del tipo especificado cuya fecha sea mayor o igual a {@code from}.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts/{id}/movements?from=2024-01-01&type=DEPOSIT}
 * solicitando los depósitos realizados desde una fecha específica.</p>
 *
 * @see MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByFromAndTypeMovementStrategy implements MovementFilterStrategy {

    private final MovementPersistencePort movementPersistencePort;
    private final String accountId;
    private final LocalDateTime from;
    private final MovementType type;

    @Override
    public Flowable<Movement> find() {
        return Flowable.defer(() ->
                movementPersistencePort.findAllByAccountIdOrderByOccurredAtDesc(accountId)
                        .filter(m -> m.getOccurredAt() != null
                                && !m.getOccurredAt().isBefore(from)
                                && type.equals(m.getType()))
        ).subscribeOn(Schedulers.io());
    }
}
