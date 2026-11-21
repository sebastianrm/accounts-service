package com.yanki.accounts.interfaces.rest.dto;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.service.MovementResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response de movimiento bancario.
 *
 * <p>DTO de salida que contiene los detalles de un movimiento registrado,
 * incluyendo tipo, importe, saldo resultante, descripción y metadatos de auditoría.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "MovementResponse", description = "Respuesta con los detalles de un movimiento bancario.", example = "{\"movementId\":\"MOV001\",\"accountId\":\"ACC001\",\"movementType\":\"DEPOSIT\",\"amount\":500.00,\"balanceAfter\":1500.00,\"commission\":0.00,\"description\":\"Depósito de sueldo\",\"occurredAt\":\"2026-11-14T10:30:00\"}")
public class MovementResponse {

    @Schema(description = "Identificador único del movimiento.", example = "MOV001")
    private String movementId;

    @Schema(description = "Identificador de la cuenta asociada al movimiento.", example = "ACC001")
    private String accountId;

    @Schema(description = "Tipo de movimiento realizado.", example = "DEPOSIT", allowableValues = {"DEPOSIT", "WITHDRAWAL", "TRANSFER_IN", "TRANSFER_OUT", "COMMISSION", "PAYMENT", "INTEREST"})
    private String movementType;

    @Schema(description = "Monto del movimiento.", example = "500.00")
    private BigDecimal amount;

    @Schema(description = "Saldo de la cuenta después de ejecutar el movimiento.", example = "1500.00")
    private BigDecimal balanceAfter;

    @Schema(description = "Comisión aplicada al movimiento. 0 si no se aplicó comisión.", example = "0.00")
    private BigDecimal commission;

    @Schema(description = "Descripción del motivo del movimiento.", example = "Depósito de sueldo")
    private String description;

    @Schema(description = "Fecha y hora en que ocurrió el movimiento.", example = "2026-11-14T10:30:00")
    private LocalDateTime occurredAt;

    public static MovementResponse from(MovementResult result) {
        return MovementResponse.builder()
                .movementId(result.getMovementId())
                .accountId(result.getAccountId())
                .movementType(result.getMovementType())
                .amount(result.getAmount())
                .balanceAfter(result.getBalanceAfter())
                .commission(result.getCommission())
                .description(result.getDescription())
                .occurredAt(result.getOccurredAt())
                .build();
    }

    /**
     * Mapea una entidad Movement del dominio a MovementResponse.
     *
     * <p>Se utiliza en el endpoint de listado de movimientos donde el resultado
     * proviene directamente del repositorio y no de un MovementResult.</p>
     *
     * @param movement entidad de movimiento del dominio
     * @return DTO de respuesta para la API REST
     */
    public static MovementResponse fromMovement(Movement movement) {
        return MovementResponse.builder()
                .movementId(movement.getId())
                .accountId(movement.getAccountId())
                .movementType(movement.getType() != null ? movement.getType().name() : null)
                .amount(movement.getAmount())
                .balanceAfter(movement.getBalanceAfter())
                .commission(movement.getCommission())
                .description(movement.getDescription())
                .occurredAt(movement.getOccurredAt())
                .build();
    }
}