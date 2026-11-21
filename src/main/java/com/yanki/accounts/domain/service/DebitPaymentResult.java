package com.yanki.accounts.domain.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de la liquidación de un pago con tarjeta de débito.
 *
 * <p>DTO que indica si el pago fue procesado exitosamente ({@code SETTLED})
 * o rechazado ({@code REJECTED}), incluyendo el motivo del rechazo si aplica.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebitPaymentResult {

    /** Identificador del pago. */
    private String paymentId;

    /** Estado: SETTLED, REJECTED. */
    private String status;

    /** Motivo de rechazo (si aplica). */
    private String reason;
}