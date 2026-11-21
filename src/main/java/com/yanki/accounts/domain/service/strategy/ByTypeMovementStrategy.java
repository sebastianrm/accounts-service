package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar movimientos por tipo (type).
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.MovementFilterCriteria}
 * contiene solo {@code type} activo. Filtra los movimientos que coinciden con el tipo
 * especificado (DEPOSIT, WITHDRAWAL, TRANSFER_IN, etc.).</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts/{id}/movements?type=DEPOSIT}
 * solicitando solo los depósitos realizados en la cuenta.</p>
 *
 * @see MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByTypeMovementStrategy implements MovementFilterStrategy {

    private final MovementPersistencePort movementPersistencePort;
    private final String accountId;
    private final MovementType type;

    @Override
    public Flowable<Movement> find() {
        return Flowable.defer(() ->
                movementPersistencePort.findAllByAccountIdOrderByOccurredAtDesc(accountId)
                        .filter(m -> type.equals(m.getType()))
        ).subscribeOn(Schedulers.io());
    }
}
