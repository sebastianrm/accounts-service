package com.yanki.accounts.domain.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de la liquidación de una transferencia bancaria.
 *
 * <p>DTO que indica si la transferencia fue completada exitosamente ({@code COMPLETED})
 * o rechazada ({@code REJECTED}), incluyendo el motivo del rechazo si aplica.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResult {

    /** Identificador de la transferencia. */
    private String transferId;

    /** Estado: COMPLETED, REJECTED. */
    private String status;

    /** Motivo de rechazo (si aplica). */
    private String reason;
}