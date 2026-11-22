package com.yanki.accounts.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request para depósito en cuenta bancaria.
 *
 * <p>Contiene el importe a acreditar, descripción opcional, canal de origen
 * y referencia externa para trazabilidad.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DepositRequest", description = "Solicitud para realizar un depósito en una cuenta bancaria.", example = "{\"amount\":500.00,\"description\":\"Depósito de sueldo\",\"channel\":\"WEB\",\"referenceId\":\"REF001\"}")
public class DepositRequest {

    @NotNull(message = "Importe es obligatorio")
    @DecimalMin(value = "0.01", message = "Importe debe ser mayor a 0")
    @DecimalMax(value = "1000000.00", message = "Importe excede el límite permitido de S/ 1,000,000.00")
    @Schema(description = "Monto a depositar. Debe ser mayor a 0.", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @Schema(description = "Descripción opcional del motivo del depósito.", example = "Depósito de sueldo", nullable = true)
    private String description;

    @NotBlank(message = "Canal es obligatorio")
    @Schema(description = "Canal desde donde se realiza el depósito.", example = "WEB", allowableValues = {"BRANCH", "ATM", "MOBILE", "WEB"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String channel;

    @Schema(description = "Referencia externa para trazabilidad del depósito.", example = "REF001", nullable = true)
    private String referenceId;
}