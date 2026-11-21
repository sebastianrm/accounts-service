package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Estrategia para filtrar movimientos por fecha de fin y tipo (to y type).
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.MovementFilterCriteria}
 * contiene {@code to} y {@code type} activos, mientras {@code from} es null.
 * Filtra los movimientos del tipo especificado cuya fecha sea menor o igual a {@code to}.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts/{id}/movements?to=2024-06-30&type=WITHDRAWAL}
 * solicitando los retiros realizados hasta una fecha específica.</p>
 *
 * @see MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByToAndTypeMovementStrategy implements MovementFilterStrategy {

    private final MovementPersistencePort movementPersistencePort;
    private final String accountId;
    private final LocalDateTime to;
    private final MovementType type;

    @Override
    public Flowable<Movement> find() {
        return Flowable.defer(() ->
                movementPersistencePort.findAllByAccountIdOrderByOccurredAtDesc(accountId)
                        .filter(m -> m.getOccurredAt() != null
                                && !m.getOccurredAt().isAfter(to)
                                && type.equals(m.getType()))
        ).subscribeOn(Schedulers.io());
    }
}
