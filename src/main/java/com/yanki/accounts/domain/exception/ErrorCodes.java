package com.yanki.accounts.domain.exception;

/**
 * Códigos de error estandarizados para accounts-service.
 *
 * <p>Cada código corresponde a una validación de regla de negocio
 * y se mapea a un HTTP status en el {@link com.yanki.accounts.interfaces.rest.GlobalExceptionHandler}.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public final class ErrorCodes {

    // Cuenta
    public static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
    public static final String ACCOUNT_ALREADY_CLOSED = "ACCOUNT_ALREADY_CLOSED";
    public static final String ACCOUNT_BLOCKED = "ACCOUNT_BLOCKED";
    public static final String ACCOUNT_INSUFFICIENT_BALANCE = "ACCOUNT_INSUFFICIENT_BALANCE";
    public static final String ACCOUNT_CANNOT_CLOSE_BALANCE_NOT_ZERO = "ACCOUNT_CANNOT_CLOSE_BALANCE_NOT_ZERO";

    // Validaciones de tipo/cliente
    public static final String SAVINGS_ACCOUNT_ALREADY_EXISTS = "SAVINGS_ACCOUNT_ALREADY_EXISTS";
    public static final String CURRENT_ACCOUNT_PERSONAL_LIMIT = "CURRENT_ACCOUNT_PERSONAL_LIMIT";
    public static final String TERM_FIXED_ACCOUNT_ALREADY_EXISTS = "TERM_FIXED_ACCOUNT_ALREADY_EXISTS";
    public static final String ENTERPRISE_CANNOT_HAVE_SAVINGS = "ENTERPRISE_CANNOT_HAVE_SAVINGS";
    public static final String ENTERPRISE_CANNOT_HAVE_TERM_FIXED = "ENTERPRISE_CANNOT_HAVE_TERM_FIXED";

    // Movimientos
    public static final String MOVEMENT_DAY_NOT_ALLOWED = "MOVEMENT_DAY_NOT_ALLOWED";
    public static final String MOVEMENT_ALREADY_DONE_TODAY = "MOVEMENT_ALREADY_DONE_TODAY";
    public static final String EXCEEDED_FREE_MOVEMENTS = "EXCEEDED_FREE_MOVEMENTS";
    public static final String INVALID_MOVEMENT_AMOUNT = "INVALID_MOVEMENT_AMOUNT";

    // VIP/PYME requirements
    public static final String CREDIT_CARD_REQUIRED_FOR_VIP = "CREDIT_CARD_REQUIRED_FOR_VIP";
    public static final String CREDIT_CARD_REQUIRED_FOR_PYME = "CREDIT_CARD_REQUIRED_FOR_PYME";

    // Riesgo
    public static final String CUSTOMER_HAS_OVERDUE_DEBT = "CUSTOMER_HAS_OVERDUE_DEBT";
    public static final String ACCOUNT_BLOCKED_RISK = "ACCOUNT_BLOCKED_RISK";

    // Transferencias / Débitos
    public static final String TRANSFER_SAME_ACCOUNT = "TRANSFER_SAME_ACCOUNT";
    public static final String DEBIT_PAYMENT_REJECTED = "DEBIT_PAYMENT_REJECTED";

    // Validaciones generales
    public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";
    public static final String CUSTOMER_INACTIVE = "CUSTOMER_INACTIVE";
    public static final String INVALID_ACCOUNT_TYPE = "INVALID_ACCOUNT_TYPE";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    private ErrorCodes() {
    }
}