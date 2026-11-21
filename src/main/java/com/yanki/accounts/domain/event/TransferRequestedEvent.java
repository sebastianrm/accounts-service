package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de transferencia solicitada (proveniente de transfers-service).
 *
 * <p>Topic: {@code yanki.transfer.requested.v1}</p>
 *
 * <p>Se consume para liquidar la transferencia debitando la cuenta origen
 * y acreditando la cuenta destino, validando saldos y estados.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestedEvent {

    /** Identificador de la transferencia. */
    private String transferId;

    /** Cuenta origen. */
    private String sourceAccountId;

    /** Cuenta destino. */
    private String targetAccountId;

    /** Importe. */
    private BigDecimal amount;

    /** Descripción. */
    private String description;

    /** Cliente solicitante. */
    private String requestedBy;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}