package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Evento de dominio que se publica cuando el job de comisiones de mantenimiento
 * no puede aplicar la comisión mensual porque el saldo de la cuenta es insuficiente.
 *
 * <p>Topic: {@link Topics#ACCOUNT_MAINTENANCE_FAILED}</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Builder
@AllArgsConstructor
public class MaintenanceFailedEvent {

    /**
     * Identificador único de la cuenta afectada.
     */
    private final String accountId;

    /**
     * Identificador del cliente propietario de la cuenta.
     */
    private final String customerId;

    /**
     * Monto de la comisión de mantenimiento que se intentó aplicar.
     */
    private final BigDecimal commissionAmount;

    /**
     * Saldo actual de la cuenta (insuficiente para cubrir la comisión).
     */
    private final BigDecimal currentBalance;

    /**
     * Mes al que corresponde la comisión (formato yyyy-MM).
     */
    private final String month;

    /**
     * Motivo del fallo (ej. "SALDO_INSUFICIENTE").
     */
    private final String reason;

    /**
     * Fecha y hora en que se intentó aplicar la comisión.
     */
    private final LocalDateTime occurredAt;
}
