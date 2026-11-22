package com.yanki.accounts.infrastructure.persistence.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Documento MongoDB para la colección de cuentas bancarias.
 *
 * <p>Equivalente de persistencia de {@link com.yanki.accounts.domain.model.Account}.
 * Contiene las anotaciones de Spring Data MongoDB necesarias para el mapeo.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "accounts")
@CompoundIndex(name = "idx_customerId_type", def = "{'customerId': 1, 'type': 1}")
@CompoundIndex(name = "idx_customerId_status", def = "{'customerId': 1, 'status': 1}")
public class AccountDocument {

    /** Identificador unico de la cuenta y ID de la coleccion MongoDB. */
    @Id
    private String id;

    /** Identificador del cliente propietario. */
    @Indexed
    private String customerId;

    /** Número de cuenta generado (formato: ACC + 16 chars). */
    @Indexed(unique = true)
    private String accountNumber;

    /** Tipo de producto: SAVINGS, CURRENT, TERM_FIXED. */
    @Indexed
    private String type;

    /** Estado de la cuenta: OPEN, CLOSED, BLOCKED. */
    @Indexed
    private String status;

    /** Versión para control de concurrencia optimista. */
    @Version
    private Long version;

    /** Moneda de la cuenta (PEN, USD). */
    private String currency;

    /** Saldo actual disponible. */
    private BigDecimal balance;

    /** Monto mínimo de apertura (puede ser 0). */
    private BigDecimal minOpeningAmount;

    /** Máximo de movimientos gratuitos por mes (solo ahorro). */
    private Integer maxFreeMovementsPerMonth;

    /** Comisión por movimiento excedente (solo ahorro). */
    private BigDecimal commissionPerExcessMovement;

    /** Comisión de mantenimiento mensual (solo corriente). */
    private BigDecimal maintenanceCommission;

    /** Fecha de vencimiento (solo plazo fijo). */
    private LocalDateTime maturityDate;

    /** Día del mes permitido para movimiento (solo plazo fijo, 1-28). */
    private Integer allowedMovementDay;

    /** Titulares de la cuenta (solo corriente empresarial). */
    @Field("holders")
    private List<AccountHolderDocument> holders;

    /** Firmantes autorizados (solo corriente empresarial). */
    @Field("signers")
    private List<AccountSignerDocument> signers;

    /** Fecha de apertura. */
    private LocalDateTime openedAt;

    /** Fecha de cierre. */
    private LocalDateTime closedAt;

    /** Fecha de creación del registro. */
    private LocalDateTime createdAt;

    /** Fecha de última actualización. */
    private LocalDateTime updatedAt;

    /** Contador de movimientos del mes actual. */
    @Builder.Default
    private Integer monthlyMovementCount = 0;

    /** Mes/año del contador actual (formato yyyy-MM). */
    private String currentMonth;

    /**
     * Documento anidado para titulares de cuenta.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountHolderDocument {
        private String holderId;
        private BigDecimal percentage;
        private Boolean principal;
    }

    /**
     * Documento anidado para firmantes autorizados.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSignerDocument {
        private String signerId;
        private BigDecimal operationLimit;
        private Boolean active;
    }
}
