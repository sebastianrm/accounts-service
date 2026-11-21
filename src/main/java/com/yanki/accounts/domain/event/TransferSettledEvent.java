package com.yanki.accounts.domain.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento publicado cuando se liquida una transferencia (confirmada o rechazada).
 *
 * <p>Topic: {@code yanki.transfer.settled.v1}
 * Key: transferId</p>
 *
 * <p>Se publica tras procesar una transferencia solicitada desde transfers-service,
 * indicando si fue completada (COMPLETED) o rechazada (REJECTED).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferSettledEvent {

    /** Identificador de la transferencia. */
    private String transferId;

    /** Estado: COMPLETED, REJECTED. */
    private String status;
    
    /** Cuenta de origen. */
    private String sourceAccountId;

    /** Motivo de rechazo (si aplica). */
    private String reason;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}
