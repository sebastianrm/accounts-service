package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar cuentas por cliente y tipo simultáneamente.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene {@code customerId} y {@code type} activos, mientras {@code status} es null.
 * Delega en {@code accountPersistencePort.findAllByCustomerIdAndType()} para obtener las cuentas
 * de un cliente específico con un tipo determinado.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts?customerId=123&type=SAVINGS}
 * solicitando las cuentas de ahorro de un cliente sin filtrar por estado.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByCustomerAndTypeStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final String customerId;
    private final AccountType type;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByCustomerIdAndType(customerId, type)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
