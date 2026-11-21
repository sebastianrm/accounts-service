package com.yanki.accounts.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response de validación de cuenta para transferencias inter-servicio.
 *
 * <p>Utilizado por transfers-service para validar que una cuenta existe, está operativa
 * y cumple los requisitos antes de iniciar una transferencia. Incluye saldo, tipo de producto,
 * límites de movimientos y estado de deuda vencida del cliente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AccountValidationResponse", description = "Respuesta de validación de cuenta para transferencias.",
        example = "{\"valid\":true,\"accountId\":\"ACC001\",\"customerId\":\"CUST001\",\"status\":\"OPEN\",\"balance\":3000.00,\"productType\":\"SAVINGS\",\"maxFreeMovements\":5,\"commissionPerExcessMovement\":2.50,\"hasOverdueDebt\":false}")
public class AccountValidationResponse {

    @Schema(description = "Indica si la cuenta es válida para realizar transferencias.", example = "true")
    private boolean valid;

    @Schema(description = "Identificador único de la cuenta.", example = "ACC001")
    private String accountId;

    @Schema(description = "Identificador del cliente propietario.", example = "CUST001")
    private String customerId;

    @Schema(description = "Estado de la cuenta.", example = "OPEN", allowableValues = {"OPEN", "CLOSED", "BLOCKED"})
    private String status;

    @Schema(description = "Saldo disponible actual de la cuenta.", example = "3000.00")
    private BigDecimal balance;

    @Schema(description = "Tipo de producto de la cuenta.", example = "SAVINGS", allowableValues = {"SAVINGS", "CURRENT", "TERM_FIXED"})
    private String productType;

    @Schema(description = "Máximo de movimientos gratuitos por mes (null si no aplica).", example = "5")
    private Integer maxFreeMovements;

    @Schema(description = "Comisión por movimiento excedente (null si no aplica).", example = "2.50")
    private BigDecimal commissionPerExcessMovement;

    @Schema(description = "Indica si el cliente tiene deuda vencida.", example = "false")
    private boolean hasOverdueDebt;
}
