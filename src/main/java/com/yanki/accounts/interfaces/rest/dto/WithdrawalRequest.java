package com.yanki.accounts.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request para retiro de cuenta bancaria.
 *
 * <p>Contiene el importe a debitar, descripción opcional, canal de origen
 * y referencia externa para trazabilidad.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "WithdrawalRequest", description = "Solicitud para realizar un retiro de una cuenta bancaria.", example = "{\"amount\":200.00,\"description\":\"Retiro en cajero\",\"channel\":\"ATM\",\"referenceId\":\"REF002\"}")
public class WithdrawalRequest {

    @NotNull(message = "Importe es obligatorio")
    @DecimalMin(value = "0.01", message = "Importe debe ser mayor a 0")
    @Schema(description = "Monto a retirar. Debe ser mayor a 0.", example = "200.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Descripción opcional del motivo del retiro.", example = "Retiro en cajero", nullable = true)
    private String description;

    @NotBlank(message = "Canal es obligatorio")
    @Schema(description = "Canal desde donde se realiza el retiro.", example = "ATM", allowableValues = {"BRANCH", "ATM", "MOBILE", "WEB"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String channel;

    @Schema(description = "Referencia externa para trazabilidad del retiro.", example = "REF002", nullable = true)
    private String referenceId;
}