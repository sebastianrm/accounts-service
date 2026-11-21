package com.yanki.accounts.infrastructure.persistence.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB para la vista local de riesgo del cliente.
 *
 * <p>Equivalente de persistencia de {@link com.yanki.accounts.domain.model.CustomerRiskView}.
 * Se alimenta con eventos Kafka de credits-service.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_risk_view")
public class CustomerRiskViewDocument {

    /** Identificador interno del documento. */
    @Id
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
