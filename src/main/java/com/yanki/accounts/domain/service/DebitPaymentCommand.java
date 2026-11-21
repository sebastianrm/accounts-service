package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Comando para liquidar un pago con tarjeta de débito.
 *
 * <p>Encapsula los datos necesarios para procesar un pago solicitado desde
 * cards-service, incluyendo la identificación del pago, tarjeta, cliente,
 * cuenta asociada y el importe a debitar.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitPaymentCommand {

    /** Identificador del pago. */
    private String paymentId;

    /** Identificador de la tarjeta. */
    private String cardId;

    /** Identificador del cliente. */
    private String customerId;

    /** Identificador de la cuenta asociada. */
    private String accountId;

    /** Importe. */
    private BigDecimal amount;

    /** Fecha del evento (para idempotencia). */
    private LocalDateTime occurredAt;
}