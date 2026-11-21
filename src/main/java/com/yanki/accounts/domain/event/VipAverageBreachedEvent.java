package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Evento de dominio que se publica cuando el job de verificación de promedio VIP
 * detecta que el promedio diario de una cuenta VIP está por debajo del mínimo requerido.
 *
 * <p>Topic: {@link Topics#VIP_AVERAGE_BREACHED}</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Builder
@AllArgsConstructor
public class VipAverageBreachedEvent {

    /**
     * Identificador único de la cuenta afectada.
     */
    private final String accountId;

    /**
     * Identificador del cliente propietario de la cuenta.
     */
    private final String customerId;

    /**
     * Promedio diario calculado del mes anterior.
     */
    private final BigDecimal dailyAverage;

    /**
     * Promedio mínimo requerido para mantener el perfil VIP.
     */
    private final BigDecimal minRequiredAverage;

    /**
     * Monto de la penalidad aplicada.
     */
    private final BigDecimal penaltyAmount;

    /**
     * Saldo de la cuenta después de aplicar la penalidad.
     */
    private final BigDecimal balanceAfter;

    /**
     * Mes al que corresponde el cálculo (formato yyyy-MM).
     */
    private final String month;

    /**
     * Fecha y hora en que se detectó el incumplimiento.
     */
    private final LocalDateTime occurredAt;
}
