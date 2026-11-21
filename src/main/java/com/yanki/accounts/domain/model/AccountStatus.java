package com.yanki.accounts.domain.model;

/**
 * Enumeración de estados de una cuenta bancaria.
 *
 * <p>Define los estados posibles del ciclo de vida de una cuenta:</p>
 * <ul>
 *   <li>{@link #OPEN}: Cuenta operativa y disponible para movimientos</li>
 *   <li>{@link #CLOSED}: Cuenta cerrada definitivamente (balance debe ser 0)</li>
 *   <li>{@link #BLOCKED}: Cuenta bloqueada temporalmente (no permite movimientos)</li>
 * </ul>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public enum AccountStatus {
    /** Cuenta operativa y disponible para movimientos. */
    OPEN,

    /** Cuenta cerrada definitivamente (balance debe ser 0). */
    CLOSED,

    /** Cuenta bloqueada temporalmente (no permite movimientos). */
    BLOCKED;
}