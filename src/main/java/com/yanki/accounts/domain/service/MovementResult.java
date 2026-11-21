package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de un movimiento bancario registrado.
 *
 * <p>DTO que contiene la confirmación de un movimiento ejecutado exitosamente,
 * incluyendo el identificador generado, el saldo resultante y cualquier comisión aplicada.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementResult {

    /** Identificador del movimiento. */
    private String movementId;

    /** Identificador de la cuenta. */
    private String accountId;

    /** Tipo de movimiento. */
    private String movementType;

    /** Importe. */
    private BigDecimal amount;

    /** Saldo después del movimiento. */
    private BigDecimal balanceAfter;

    /** Comisión aplicada. */
    private BigDecimal commission;

    /** Descripción. */
    private String description;

    /** Fecha del movimiento. */
    private LocalDateTime occurredAt;
}