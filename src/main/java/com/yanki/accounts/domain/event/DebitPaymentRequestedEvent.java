package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de pago con tarjeta de débito solicitado (proveniente de cards-service).
 *
 * <p>Topic: {@code yanki.debit.payment.requested.v1}</p>
 *
 * <p>Se consume para liquidar el pago debitando la cuenta asociada a la tarjeta,
 * validando saldo suficiente y estado activo de la cuenta.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DebitPaymentRequestedEvent {

    /** Identificador del pago. */
    private String paymentId;

    /** Identificador de la tarjeta. */
    private String cardId;

    /** Identificador del cliente. */
    private String customerId;

    /** Identificador de la cuenta. */
    private String accountId;

    /** Importe. */
    private BigDecimal amount;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}