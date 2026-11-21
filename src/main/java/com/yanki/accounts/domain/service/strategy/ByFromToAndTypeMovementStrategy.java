package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Estrategia para filtrar movimientos por rango de fechas y tipo (from, to y type).
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.MovementFilterCriteria}
 * contiene los tres parámetros activos: {@code from}, {@code to} y {@code type}.
 * Filtra los movimientos del tipo especificado dentro del rango de fechas proporcionado.</p>
 *
 * <p>Caso de uso típico: el cliente invoca
 * {@code GET /api/accounts/{id}/movements?from=2024-01-01&to=2024-03-31&type=DEPOSIT}
 * solicitando los depósitos de un trimestre específico.</p>
 *
 * @see MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByFromToAndTypeMovementStrategy implements MovementFilterStrategy {

    private final MovementPersistencePort movementPersistencePort;
    private final String accountId;
    private final LocalDateTime from;
    private final LocalDateTime to;
    private final MovementType type;

    @Override
    public Flowable<Movement> find() {
        return Flowable.defer(() ->
                movementPersistencePort.findAllByAccountIdAndOccurredAtBetween(accountId, from, to)
                        .filter(m -> type.equals(m.getType()))
        ).subscribeOn(Schedulers.io());
    }
}
