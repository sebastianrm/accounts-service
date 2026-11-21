package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar cuentas por cliente únicamente.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene solo {@code customerId} activo, mientras {@code type} y {@code status} son null.
 * Delega en {@code accountPersistencePort.findAllByCustomerId()} para obtener las cuentas
 * de un cliente específico.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts?customerId=123}
 * solicitando todas las cuentas de un cliente sin filtrar por tipo o estado.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByCustomerStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final String customerId;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByCustomerId(customerId)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
