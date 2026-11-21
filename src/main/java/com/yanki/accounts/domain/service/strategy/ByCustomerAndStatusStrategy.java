package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar cuentas por cliente y estado simultáneamente.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene {@code customerId} y {@code status} activos, mientras {@code type} es null.
 * Delega en {@code accountPersistencePort.findAllByCustomerIdAndStatus()} para obtener las cuentas
 * de un cliente específico con un estado determinado.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts?customerId=123&status=OPEN}
 * solicitando las cuentas activas de un cliente sin filtrar por tipo.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByCustomerAndStatusStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final String customerId;
    private final AccountStatus status;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByCustomerIdAndStatus(customerId, status)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
