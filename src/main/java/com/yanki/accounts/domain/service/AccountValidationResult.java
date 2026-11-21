package com.yanki.accounts.domain.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Resultado de la validación de una cuenta para transferencias inter-servicio.
 *
 * <p>Utilizado por transfers-service para verificar que una cuenta existe, está operativa
 * y cumple los requisitos antes de iniciar una transferencia. Incluye saldo, tipo de producto,
 * límites de movimientos y estado de deuda vencida del cliente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountValidationResult {

    /** Indica si la cuenta es válida para realizar transferencias. */
    private boolean valid;

    /** Identificador único de la cuenta. */
    private String accountId;

    /** Identificador del cliente propietario. */
    private String customerId;

    /** Estado de la cuenta (OPEN, CLOSED, BLOCKED). */
    private String status;

    /** Saldo disponible actual de la cuenta. */
    private BigDecimal balance;

    /** Tipo de producto de la cuenta (SAVINGS, CURRENT, TERM_FIXED). */
    private String productType;

    /** Máximo de movimientos gratuitos por mes (null si no aplica). */
    private Integer maxFreeMovements;

    /** Comisión por movimiento excedente (null si no aplica). */
    private BigDecimal commissionPerExcessMovement;

    /** Indica si el cliente tiene deuda vencida. */
    private boolean hasOverdueDebt;
}
