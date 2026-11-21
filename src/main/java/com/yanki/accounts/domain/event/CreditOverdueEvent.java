package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de deuda vencida (proveniente de credits-service).
 *
 * <p>Topic: {@code yanki.credit.overdue.v1}</p>
 *
 * <p>Se consume para actualizar la {@link com.yanki.accounts.domain.model.CustomerRiskView}
 * local, bloqueando la apertura de nuevos productos si el cliente tiene
 * deuda vencida (>30 días de mora).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditOverdueEvent {

    /** Identificador del cliente. */
    private String customerId;

    /** Identificador del crédito. */
    private String creditId;

    /** Monto vencido. */
    private BigDecimal overdueAmount;

    /** Días de mora. */
    private Integer daysOverdue;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}