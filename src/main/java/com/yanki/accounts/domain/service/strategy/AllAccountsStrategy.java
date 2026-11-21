package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para devolver todas las cuentas sin aplicar ningún filtro.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria} no
 * contiene ningún parámetro activo ({@code customerId}, {@code type} y {@code status} son null).
 * Delega en {@code accountPersistencePort.findAll()} para obtener el conjunto completo de cuentas.</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts} sin query parameters,
 * solicitando la lista completa de cuentas del sistema.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class AllAccountsStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAll()
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
