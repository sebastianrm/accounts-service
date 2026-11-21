package com.yanki.accounts.domain.event;

/**
 * Constantes con los nombres de los topics Kafka para accounts-service.
 *
 * <p>Centraliza la definición de todos los topics de eventos que el servicio
 * produce y consume, evitando errores de tipeo y facilitando el mantenimiento.</p>
 *
 * <p>Topics producidos (outbound):</p>
 * <ul>
 *   <li>{@link #ACCOUNT_OPENED}: Apertura exitosa de cuenta</li>
 *   <li>{@link #ACCOUNT_CLOSED}: Cierre exitoso de cuenta</li>
 *   <li>{@link #ACCOUNT_MOVEMENT_REGISTERED}: Registro de movimiento (6 particiones)</li>
 *   <li>{@link #DEBIT_PAYMENT_SETTLED}: Liquidación de pago débito</li>
 *   <li>{@link #TRANSFER_SETTLED}: Liquidación de transferencia</li>
 *   <li>{@link #ACCOUNT_MAINTENANCE_APPLIED}: Comisión mantenimiento aplicada (job)</li>
 *   <li>{@link #ACCOUNT_MAINTENANCE_FAILED}: Comisión mantenimiento fallida (job)</li>
 *   <li>{@link #VIP_AVERAGE_BREACHED}: Promedio VIP por debajo del mínimo (job)</li>
 * </ul>
 *
 * <p>Topics consumidos (inbound):</p>
 * <ul>
 *   <li>{@link #CUSTOMER_REGISTERED}, {@link #CUSTOMER_UPDATED}, {@link #CUSTOMER_DELETED}: Perfil del cliente</li>
 *   <li>{@link #CARD_OPENED}, {@link #CARD_CLOSED}: Estado de tarjetas</li>
 *   <li>{@link #DEBIT_PAYMENT_REQUESTED}: Solicitud de pago débito</li>
 *   <li>{@link #TRANSFER_REQUESTED}: Solicitud de transferencia</li>
 *   <li>{@link #CREDIT_OVERDUE}: Deuda vencida (riesgo)</li>
 * </ul>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public final class Topics {

    // Producidos por accounts-service
    public static final String ACCOUNT_OPENED = "yanki.account.opened.v1";
    public static final String ACCOUNT_CLOSED = "yanki.account.closed.v1";
    public static final String ACCOUNT_MOVEMENT_REGISTERED = "yanki.account.movement.registered.v1";
    public static final String DEBIT_PAYMENT_SETTLED = "yanki.debit.payment.settled.v1";
    public static final String TRANSFER_SETTLED = "yanki.transfer.settled.v1";

    // Producidos por jobs programados
    public static final String ACCOUNT_MAINTENANCE_APPLIED = "yanki.account.maintenance.applied.v1";
    public static final String ACCOUNT_MAINTENANCE_FAILED = "yanki.account.maintenance.failed.v1";
    public static final String VIP_AVERAGE_BREACHED = "yanki.vip.average.breached.v1";

    // Consumidos por accounts-service
    public static final String CUSTOMER_REGISTERED = "yanki.customer.registered.v1";
    public static final String CUSTOMER_UPDATED = "yanki.customer.updated.v1";
    public static final String CUSTOMER_DELETED = "yanki.customer.deleted.v1";
    public static final String CARD_OPENED = "yanki.card.opened.v1";
    public static final String CARD_CLOSED = "yanki.card.closed.v1";
    public static final String DEBIT_PAYMENT_REQUESTED = "yanki.debit.payment.requested.v1";
    public static final String TRANSFER_REQUESTED = "yanki.transfer.requested.v1";
    public static final String CREDIT_OVERDUE = "yanki.credit.overdue.v1";

    private Topics() {
    }
}