package com.yanki.accounts.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad pura de dominio que representa un movimiento bancario en una cuenta.
 *
 * <p>Registra todas las operaciones de débito y crédito: depósitos, retiros,
 * transferencias, comisiones, intereses y pagos con tarjeta débito.
 * Cada movimiento genera un evento Kafka para trazabilidad completa.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Movement {

    /** Identificador del movimiento. */
    private String id;

    /** Identificador de la cuenta. */
    private String accountId;

    /** Identificador del cliente (denormalizado para consultas). */
    private String customerId;

    /** Tipo de movimiento. */
    private MovementType type;

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