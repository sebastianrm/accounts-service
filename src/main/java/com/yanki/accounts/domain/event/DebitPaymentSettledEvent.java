package com.yanki.accounts.domain.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento publicado cuando se liquida un pago con tarjeta de débito.
 *
 * <p>Topic: {@code yanki.debit.payment.settled.v1}
 * Key: paymentId</p>
 *
 * <p>Se publica tras procesar un pago débito solicitado desde cards-service,
 * indicando si fue aceptado (SETTLED) o rechazado (REJECTED).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitPaymentSettledEvent {

    /** Identificador del pago. */
    private String paymentId;

    /** Identificador de la tarjeta. */
    private String cardId;

    /** Estado: SETTLED, REJECTED. */
    private String status;

    /** Motivo de rechazo (si aplica). */
    private String reason;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}