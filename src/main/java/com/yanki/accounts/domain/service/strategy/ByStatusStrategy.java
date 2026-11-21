package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;

/**
 * Estrategia para filtrar cuentas por estado únicamente.
 *
 * <p>Se selecciona cuando {@link com.yanki.accounts.domain.service.AccountFilterCriteria}
 * contiene solo {@code status} activo, mientras {@code customerId} y {@code type} son null.
 * Delega en {@code accountPersistencePort.findAllByStatus()} para obtener las cuentas
 * con un estado específico (OPEN, CLOSED, BLOCKED).</p>
 *
 * <p>Caso de uso típico: el cliente invoca {@code GET /api/accounts?status=OPEN}
 * solicitando todas las cuentas activas del sistema sin filtrar por cliente o tipo.</p>
 *
 * @see AccountFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class ByStatusStrategy implements AccountFilterStrategy {

    private final AccountPersistencePort accountPersistencePort;
    private final AccountStatus status;
    private final java.util.function.Function<Account, AccountResult> mapper;

    @Override
    public Flowable<AccountResult> find() {
        return Flowable.defer(() ->
                accountPersistencePort.findAllByStatus(status)
                        .map(a -> mapper.apply(a))
        ).subscribeOn(Schedulers.io());
    }
}
