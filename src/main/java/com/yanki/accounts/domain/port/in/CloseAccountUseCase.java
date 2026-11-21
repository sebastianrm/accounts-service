package com.yanki.accounts.domain.port.in;

import io.reactivex.rxjava3.core.Completable;

/**
 * Puerto de entrada para el cierre de cuentas.
 *
 * <p>Define el contrato de uso para cerrar una cuenta bancaria
 * sin exponer los detalles internos del servicio de dominio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface CloseAccountUseCase {

    /**
     * Cierra una cuenta bancaria.
     *
     * @param accountId identificador de la cuenta a cerrar
     * @return completable cuando la operación finaliza
     */
    Completable execute(String accountId);
}
