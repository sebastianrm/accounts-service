package com.yanki.accounts.domain.model;

/**
 * Enumeración de tipos de cuenta bancaria (productos pasivos).
 *
 * <p>Corresponde a los productos definidos en el Proyecto General vf.md:</p>
 * <ul>
 *   <li>{@link #SAVINGS}: Cuenta de ahorro - libre de comisión, límite movimientos mensuales</li>
 *   <li>{@link #CURRENT}: Cuenta corriente - comisión mantenimiento, sin límite movimientos</li>
 *   <li>{@link #TERM_FIXED}: Plazo fijo - libre de comisión, 1 movimiento/día específico</li>
 * </ul>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public enum AccountType {
    /** Cuenta de ahorro (solo PERSONAL, máx 1 por cliente). */
    SAVINGS,

    /** Cuenta corriente (PERSONAL máx 1, ENTERPRISE múltiples). */
    CURRENT,

    /** Cuenta a plazo fijo (solo PERSONAL, múltiples permitidas). */
    TERM_FIXED;
}