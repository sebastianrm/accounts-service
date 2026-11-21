package com.yanki.accounts.domain.model;

/**
 * Enumeración de tipos de movimiento bancario.
 *
 * <p>Cubre todas las operaciones posibles en una cuenta según el Proyecto General vf.md
 * y la arquitectura event-driven (Parte III).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public enum MovementType {

    // Operaciones de cliente
    /** Depósito en efectivo o cheque. */
    DEPOSIT,

    /** Retiro en efectivo. */
    WITHDRAWAL,

    // Transferencias
    /** Transferencia recibida (abono). */
    TRANSFER_IN,

    /** Transferencia enviada (cargo). */
    TRANSFER_OUT,

    // Comisiones e intereses
    /** Comisión por mantenimiento o exceso de movimientos. */
    COMMISSION,

    /** Interés crediticio (ahorro, plazo fijo). */
    INTEREST,

    // Pagos con tarjeta débito
    /** Pago con tarjeta de débito (cargo inmediato). */
    DEBIT_PAYMENT,

    // Operaciones automáticas
    /** Depósito inicial al abrir la cuenta. */
    OPENING_DEPOSIT,

    /** Ajuste por redondeo o corrección. */
    ADJUSTMENT;
}