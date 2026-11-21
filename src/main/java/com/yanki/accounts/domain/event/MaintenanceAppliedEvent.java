package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Evento de dominio que se publica cuando el job de comisiones de mantenimiento
 * aplica exitosamente la comisión mensual a una cuenta corriente.
 *
 * <p>Topic: {@link Topics#ACCOUNT_MAINTENANCE_APPLIED}</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Builder
@AllArgsConstructor
public class MaintenanceAppliedEvent {

    /**
     * Identificador único de la cuenta afectada.
     */
    private final String accountId;

    /**
     * Identificador del cliente propietario de la cuenta.
     */
    private final String customerId;

    /**
     * Monto de la comisión de mantenimiento aplicada.
     */
    private final BigDecimal commissionAmount;

    /**
     * Saldo de la cuenta después de aplicar la comisión.
     */
    private final BigDecimal balanceAfter;

    /**
     * Mes al que corresponde la comisión (formato yyyy-MM).
     */
    private final String month;

    /**
     * Fecha y hora en que se aplicó la comisión.
     */
    private final LocalDateTime occurredAt;
}
