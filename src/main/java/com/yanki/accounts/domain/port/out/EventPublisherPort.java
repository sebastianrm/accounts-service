package com.yanki.accounts.domain.port.out;

import com.yanki.accounts.domain.event.AccountClosedEvent;
import com.yanki.accounts.domain.event.AccountMovementRegisteredEvent;
import com.yanki.accounts.domain.event.AccountOpenedEvent;
import com.yanki.accounts.domain.event.DebitPaymentSettledEvent;
import com.yanki.accounts.domain.event.MaintenanceAppliedEvent;
import com.yanki.accounts.domain.event.MaintenanceFailedEvent;
import com.yanki.accounts.domain.event.TransferSettledEvent;
import com.yanki.accounts.domain.event.VipAverageBreachedEvent;
import io.reactivex.rxjava3.core.Completable;

/**
 * Puerto de salida para la publicación de eventos de dominio.
 *
 * <p>Define el contrato que el dominio necesita para publicar eventos
 * sin conocer la tecnología de mensajería subyacente (Kafka).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface EventPublisherPort {

    /**
     * Publica evento de apertura de cuenta.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishAccountOpened(AccountOpenedEvent event);

    /**
     * Publica evento de cierre de cuenta.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishAccountClosed(AccountClosedEvent event);

    /**
     * Publica evento de movimiento registrado.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishMovementRegistered(AccountMovementRegisteredEvent event);

    /**
     * Publica evento de liquidación de pago débito.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishDebitPaymentSettled(DebitPaymentSettledEvent event);

    /**
     * Publica evento de liquidación de transferencia.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishTransferSettled(TransferSettledEvent event);

    /**
     * Publica evento de comisión de mantenimiento aplicada exitosamente.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishMaintenanceApplied(MaintenanceAppliedEvent event);

    /**
     * Publica evento de comisión de mantenimiento fallida (saldo insuficiente).
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishMaintenanceFailed(MaintenanceFailedEvent event);

    /**
     * Publica evento de promedio VIP por debajo del mínimo requerido.
     *
     * @param event payload del evento
     * @return completable cuando la publicación finaliza
     */
    Completable publishVipAverageBreached(VipAverageBreachedEvent event);
}
