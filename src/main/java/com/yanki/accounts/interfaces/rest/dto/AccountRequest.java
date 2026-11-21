package com.yanki.accounts.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yanki.accounts.domain.service.OpenAccountCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request para apertura de cuenta bancaria.
 *
 * <p>Contiene los datos necesarios para crear una nueva cuenta, incluyendo
 * información del cliente, tipo de producto, monto inicial y configuración
 * de titulares y firmantes para cuentas empresariales.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AccountRequest", description = "Solicitud para abrir una nueva cuenta bancaria.", example = "{\"customerId\":\"CUST001\",\"accountType\":\"SAVINGS\",\"currency\":\"PEN\",\"initialAmount\":500.00,\"channel\":\"WEB\"}")
public class AccountRequest {

    @NotBlank(message = "Customer ID es obligatorio")
    @Schema(description = "Identificador único del cliente propietario de la cuenta.", example = "CUST001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String customerId;

    @NotBlank(message = "Tipo de cuenta es obligatorio")
    @JsonProperty("accountType")
    @Schema(description = "Tipo de cuenta a abrir. SAVINGS para clientes PERSONAL, CURRENT para PERSONAL y ENTERPRISE, TERM_FIXED para plazos fijos.", example = "SAVINGS", allowableValues = {"SAVINGS", "CURRENT", "TERM_FIXED"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String accountType;

    @NotBlank(message = "Moneda es obligatoria")
    @Schema(description = "Moneda de la cuenta. PEN para Soles Peruanos, USD para Dólares Americanos.", example = "PEN", allowableValues = {"PEN", "USD"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String currency;

    @NotNull(message = "Monto inicial es obligatorio")
    @DecimalMin(value = "0.0", inclusive = true, message = "Monto no puede ser negativo")
    @Schema(description = "Monto inicial con el que se abre la cuenta. Debe ser mayor o igual a 0.", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal initialAmount;

    @Schema(description = "Plazo en días para cuentas a plazo fijo (TERM_FIXED). Obligatorio si el tipo es TERM_FIXED.", example = "90", nullable = true)
    private Integer termDays;

    @Schema(description = "Día del mes permitido para movimientos en cuentas a plazo fijo (1-28). Obligatorio si el tipo es TERM_FIXED.", example = "15", nullable = true)
    private Integer allowedMovementDay;

    @Valid
    @Size(max = 10, message = "Máximo 10 titulares")
    @Schema(description = "Lista de titulares para cuentas empresariales (CURRENT de tipo ENTERPRISE). Máximo 10 titulares.", nullable = true)
    private List<AccountHolderRequest> holders;

    @Valid
    @Size(max = 20, message = "Máximo 20 firmantes")
    @Schema(description = "Lista de firmantes autorizados para cuentas empresariales. Máximo 20 firmantes.", nullable = true)
    private List<AccountSignerRequest> signers;

    @Schema(description = "Canal desde donde se realiza la apertura. BRANCH para sucursal, MOBILE para app móvil, WEB para banca en línea.", example = "WEB", allowableValues = {"BRANCH", "MOBILE", "WEB"})
    private String channel;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Titular de cuenta empresarial con porcentaje de propiedad.")
    public static class AccountHolderRequest {
        @Schema(description = "Identificador del titular.", example = "HOLDER001")
        private String holderId;

        @Schema(description = "Porcentaje de propiedad del titular (0-100).", example = "50")
        private Integer percentage;

        @Schema(description = "Indica si es el titular principal de la cuenta.", example = "true")
        private Boolean principal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Firmante autorizado de cuenta empresarial con límite de operación.")
    public static class AccountSignerRequest {
        @Schema(description = "Identificador del firmante.", example = "SIGNER001")
        private String signerId;

        @Schema(description = "Límite máximo de operación autorizado para el firmante.", example = "10000.00")
        private BigDecimal operationLimit;

        @Schema(description = "Indica si el firmante está activo.", example = "true")
        private Boolean active;
    }

    /**
     * Convierte el request a comando de dominio.
     */
    public OpenAccountCommand toCommand() {
        return OpenAccountCommand.builder()
                .customerId(customerId)
                .accountType(accountType)
                .currency(currency)
                .initialAmount(initialAmount)
                .termDays(termDays)
                .allowedMovementDay(allowedMovementDay)
                .holders(holders != null ? holders.stream()
                        .map(h -> OpenAccountCommand.AccountHolderCommand.builder()
                                .holderId(h.getHolderId())
                                .percentage(h.getPercentage())
                                .principal(h.getPrincipal())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .signers(signers != null ? signers.stream()
                        .map(s -> OpenAccountCommand.AccountSignerCommand.builder()
                                .signerId(s.getSignerId())
                                .operationLimit(s.getOperationLimit())
                                .active(s.getActive())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .channel(channel)
                .build();
    }
}