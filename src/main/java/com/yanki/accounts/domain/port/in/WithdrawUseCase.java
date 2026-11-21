package com.yanki.accounts.domain.port.in;

import com.yanki.accounts.domain.service.MovementResult;
import com.yanki.accounts.domain.service.WithdrawalCommand;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de entrada para retiros de cuentas.
 *
 * <p>Define el contrato de uso para debitar montos de una cuenta bancaria
 * sin exponer los detalles internos del servicio de dominio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface WithdrawUseCase {

    /**
     * Realiza un retiro de la cuenta.
     *
     * @param command datos del retiro
     * @return resultado del movimiento
     */
    Single<MovementResult> execute(WithdrawalCommand command);
}
