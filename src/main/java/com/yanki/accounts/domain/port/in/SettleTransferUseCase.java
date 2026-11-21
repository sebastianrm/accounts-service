package com.yanki.accounts.domain.port.in;

import com.yanki.accounts.domain.service.TransferCommand;
import io.reactivex.rxjava3.core.Completable;

/**
 * Puerto de entrada para la liquidación de transferencias.
 *
 * <p>Define el contrato de uso para procesar transferencias solicitadas desde transfers-service
 * sin exponer los detalles internos del servicio de dominio.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SettleTransferUseCase {

    /**
     * Liquid una transferencia bancaria.
     *
     * @param command datos de la transferencia a liquidar
     * @return completable cuando la liquidación finaliza
     */
    Completable settleTransfer(TransferCommand command);
}
