package com.yanki.accounts.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad pura de dominio que representa una cuenta bancaria en la plataforma Yanki.
 *
 * <p>Soporta tres tipos de productos pasivos definidos en {@link AccountType}:</p>
 * <ul>
 *   <li>{@code SAVINGS}: Cuenta de ahorro - libre de comisión, límite movimientos mensuales</li>
 *   <li>{@code CURRENT}: Cuenta corriente - comisión mantenimiento, sin límite movimientos</li>
 *   <li>{@code TERM_FIXED}: Plazo fijo - libre de comisión, 1 movimiento/día específico</li>
 * </ul>
 *
 * <p>Cada tipo tiene reglas de negocio específicas implementadas en {@link com.yanki.accounts.domain.service.AccountService}.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    /** Identificador único de la cuenta. */
    private String id;

    /** Identificador del cliente propietario. */
    private String customerId;

    /** Número de cuenta generado (formato: ACC + 16 chars). */
    private String accountNumber;

    /** Tipo de producto: SAVINGS, CURRENT, TERM_FIXED. */
    private AccountType type;

    /** Estado de la cuenta: OPEN, CLOSED, BLOCKED. */
    private AccountStatus status;

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
    private java.util.List<AccountHolder> holders;

    /** Firmantes autorizados (solo corriente empresarial). */
    private java.util.List<AccountSigner> signers;

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
     * Verifica si la cuenta puede realizar un movimiento.
     *
     * @return true si la cuenta está abierta y activa
     */
    public boolean canOperate() {
        return AccountStatus.OPEN.equals(this.status);
    }

    /**
     * Incrementa el contador de movimientos mensuales.
     *
     * @param now fecha actual para determinar el mes
     */
    public void incrementMovementCount(LocalDateTime now) {
        String month = now.toLocalDate().toString().substring(0, 7);
        if (!month.equals(this.currentMonth)) {
            this.currentMonth = month;
            this.monthlyMovementCount = 1;
        } else {
            this.monthlyMovementCount++;
        }
    }

    /**
     * Verifica si se ha excedido el límite de movimientos gratuitos.
     *
     * @return true si el contador supera el máximo permitido
     */
    public boolean hasExceededFreeMovements() {
        return maxFreeMovementsPerMonth != null
                && monthlyMovementCount != null
                && monthlyMovementCount > maxFreeMovementsPerMonth;
    }
}