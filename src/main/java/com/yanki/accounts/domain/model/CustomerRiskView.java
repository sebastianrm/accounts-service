package com.yanki.accounts.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vista local de riesgo del cliente (proyección desde eventos Kafka).
 *
 * <p>Se alimenta con el evento {@code yanki.credit.overdue.v1}.
 * Permite bloquear la adquisición de nuevos productos (cuentas, tarjetas)
 * si el cliente tiene deuda vencida en algún crédito (>30 días).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRiskView {

    /** Identificador interno del documento. */
    private String id;

    /** Identificador del cliente (clave única). */
    private String customerId;

    /** Indica si el cliente tiene deuda vencida. */
    @Builder.Default
    private Boolean hasOverdueDebt = false;

    /** Monto total de la deuda vencida. */
    private BigDecimal overdueAmount;

    /** Identificador del crédito que originó la deuda. */
    private String creditId;

    /** Días de mora. */
    private Integer daysOverdue;

    /** Última actualización de la vista. */
    private LocalDateTime updatedAt;
}
