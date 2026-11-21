package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Comando para liquidar una transferencia bancaria.
 *
 * <p>Encapsula los datos necesarios para procesar una transferencia solicitada desde
 * transfers-service, incluyendo las cuentas origen y destino, el importe,
 * descripción y el cliente solicitante.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferCommand {

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

    /** Fecha del evento (para idempotencia). */
    private LocalDateTime occurredAt;
}