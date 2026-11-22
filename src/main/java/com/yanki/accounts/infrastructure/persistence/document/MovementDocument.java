package com.yanki.accounts.infrastructure.persistence.document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB para la colección de movimientos bancarios.
 *
 * <p>Equivalente de persistencia de {@link com.yanki.accounts.domain.model.Movement}.
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
@Document(collection = "movements")
@CompoundIndex(name = "idx_accountId_occurredAt", def = "{'accountId': 1, 'occurredAt': -1}")
public class MovementDocument {

    /** Identificador del movimiento. */
    @Id
    private String id;

    /** Identificador de la cuenta. */
    @Indexed
    private String accountId;

    /** Identificador del cliente (denormalizado para consultas). */
    @Indexed
    private String customerId;

    /** Tipo de movimiento. */
    private String type;

    /** Importe del movimiento (positivo para abonos, negativo para cargos). */
    private BigDecimal amount;

    /** Saldo después del movimiento. */
    private BigDecimal balanceAfter;

    /** Comisión aplicada (si corresponde). */
    @Builder.Default
    private BigDecimal commission = BigDecimal.ZERO;

    /** Canal por el que se realizó: BRANCH, ATM, MOBILE, WEB, AUTO. */
    private String channel;

    /** Descripción del movimiento. */
    private String description;

    /** Identificador de referencia (transferencia, pago, etc.). */
    private String referenceId;

    /** Fecha y hora en que ocurrió el movimiento. */
    private LocalDateTime occurredAt;

    /** Fecha de creación del registro. */
    private LocalDateTime createdAt;
}
