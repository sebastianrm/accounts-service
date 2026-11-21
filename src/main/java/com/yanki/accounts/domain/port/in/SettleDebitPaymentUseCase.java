package com.yanki.accounts.domain.port.in;

import com.yanki.accounts.domain.service.DebitPaymentCommand;
import io.reactivex.rxjava3.core.Completable;

/**
 * Puerto de entrada para la liquidación de pagos con tarjeta de débito.
 *
 * <p>Define el contrato de uso para procesar pagos solicitados desde cards-service
 * sin exponer los detalles internos del servicio de dominio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SettleDebitPaymentUseCase {

    /**
     * Liquid un pago con tarjeta de débito.
     *
     * @param command datos del pago a liquidar
     * @return completable cuando la liquidación finaliza
     */
    Completable execute(DebitPaymentCommand command);
}
