package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para devolver todos los movimientos de una cuenta sin aplicar ningún filtro.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.MovementFilterCriteria} no
 * contiene ningún parámetro activo ({@code from}, {@code to} y {@code type} son null).
 * Delega en {@code movementPersistencePort.findAllByAccountIdOrderByOccurredAtDesc()} para obtener
 * todos los movimientos de la cuenta ordenados por fecha descendente.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts/{id}/movements}
 * sin query parameters, solicitando la lista completa de movimientos.</p>
 *
 * @see MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class AllMovementsStrategy implements MovementFilterStrategy {

    private final MovementPersistencePort movementPersistencePort;
    private final String accountId;

    @Override
    public Flowable<Movement> find() {
        return Flowable.defer(() ->
                movementPersistencePort.findAllByAccountIdOrderByOccurredAtDesc(accountId)
        ).subscribeOn(Schedulers.io());
    }
}
