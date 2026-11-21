package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento publicado cuando se registra un movimiento en una cuenta.
 *
 * <p>Topic: {@code yanki.account.movement.registered.v1}
 * Key: accountId (6 particiones)</p>
 *
 * <p>Se publica tras cada movimiento registrado (depósito, retiro, transferencia,
 * comisión, interés, pago débito), permitiendo a otros servicios reaccionar
 * en tiempo real a los cambios de saldo.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountMovementRegisteredEvent {

    /** Identificador del movimiento. */
    private String movementId;

    /** Identificador de la cuenta. */
    private String accountId;

    /** Identificador del cliente. */
    private String customerId;

    /** Tipo de movimiento: DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, COMMISSION, INTEREST, DEBIT_PAYMENT, OPENING_DEPOSIT. */
    private String movementType;

    /** Importe. */
    private BigDecimal amount;

    /** Saldo después del movimiento. */
    private BigDecimal balanceAfter;

    /** Comisión aplicada. */
    private BigDecimal commission;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}