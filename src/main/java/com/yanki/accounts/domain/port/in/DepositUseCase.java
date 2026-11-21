package com.yanki.accounts.domain.port.in;

import com.yanki.accounts.domain.service.DepositCommand;
import com.yanki.accounts.domain.service.MovementResult;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de entrada para depósitos en cuentas.
 *
 * <p>Define el contrato de uso para acreditar montos a una cuenta bancaria
 * sin exponer los detalles internos del servicio de dominio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface DepositUseCase {

    /**
     * Realiza un depósito en la cuenta.
     *
     * @param command datos del depósito
     * @return resultado del movimiento
     */
    Single<MovementResult> execute(DepositCommand command);
}
