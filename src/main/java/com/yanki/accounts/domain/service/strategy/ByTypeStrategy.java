package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar cuentas por tipo.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene solo {@code type} activo, mientras {@code customerId} y {@code status} son null.
 * Delega en {@code accountPersistencePort.findAllByType()} para obtener las cuentas
 * de un tipo específico.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts?type=SAVINGS}
 * solicitando todas las cuentas de ahorro sin filtrar por cliente ni estado.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByTypeStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final AccountType type;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByType(type)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
