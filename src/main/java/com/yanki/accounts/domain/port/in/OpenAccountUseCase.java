package com.yanki.accounts.domain.port.in;

import com.yanki.accounts.domain.service.AccountResult;
import com.yanki.accounts.domain.service.OpenAccountCommand;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de entrada para la apertura de cuentas.
 *
 * <p>Define el contrato de uso para abrir una nueva cuenta bancaria
 * sin exponer los detalles internos del servicio de dominio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface OpenAccountUseCase {

    /**
     * Abre una nueva cuenta bancaria.
     *
     * @param command datos de apertura de cuenta
     * @return resultado con la cuenta creada
     */
    Single<AccountResult> execute(OpenAccountCommand command);
}
