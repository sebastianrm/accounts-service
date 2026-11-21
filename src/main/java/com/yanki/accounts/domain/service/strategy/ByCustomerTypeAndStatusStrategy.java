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
 * Estrategia para filtrar cuentas por cliente, tipo y estado simultáneamente.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene los tres parámetros activos: {@code customerId}, {@code type} y {@code status}.
 * Delega en {@code accountPersistencePort.findAllByCustomerIdAndTypeAndStatus()} para obtener
 * las cuentas de un cliente específico, con un tipo y estado determinados.</p>
 *
 * <p>Caso de uso típico: el cliente invoca
 * {@code GET /api/accounts?customerId=123&type=SAVINGS&status=OPEN}
 * solicitando las cuentas de ahorro activas de un cliente específico.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByCustomerTypeAndStatusStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final String customerId;
    private final AccountType type;
    private final AccountStatus status;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByCustomerIdAndTypeAndStatus(customerId, type, status)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
