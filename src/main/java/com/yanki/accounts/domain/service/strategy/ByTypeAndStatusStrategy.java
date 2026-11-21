package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar cuentas por tipo y estado simultáneamente.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene {@code type} y {@code status} activos, mientras {@code customerId} es null.
 * Delega en {@code accountPersistencePort.findAllByTypeAndStatus()} para obtener las cuentas
 * de un tipo específico con un estado determinado.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts?type=SAVINGS&status=OPEN}
 * solicitando todas las cuentas de ahorro activas sin filtrar por cliente.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByTypeAndStatusStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final AccountType type;
    private final AccountStatus status;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByTypeAndStatus(type, status)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
