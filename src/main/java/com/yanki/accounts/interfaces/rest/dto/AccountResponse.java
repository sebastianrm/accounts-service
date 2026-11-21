package com.yanki.accounts.interfaces.rest.dto;

import com.yanki.accounts.domain.service.AccountResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response de cuenta bancaria.
 *
 * <p>DTO de salida que contiene la información completa de una cuenta,
 * incluyendo datos básicos, saldo, moneda y, para cuentas empresariales,
 * la lista de titulares y firmantes autorizados.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AccountResponse", description = "Respuesta con la información completa de una cuenta bancaria.", example = "{\"accountId\":\"ACC001\",\"accountNumber\":\"ACC123456789\",\"accountType\":\"SAVINGS\",\"status\":\"OPEN\",\"balance\":1000.00,\"currency\":\"PEN\"}")
public class AccountResponse {

    @Schema(description = "Identificador único de la cuenta.", example = "ACC001")
    private String accountId;

    @Schema(description = "Número visible de la cuenta para el cliente.", example = "ACC123456789")
    private String accountNumber;

    @Schema(description = "Tipo de cuenta.", example = "SAVINGS", allowableValues = {"SAVINGS", "CURRENT", "TERM_FIXED"})
    private String accountType;

    @Schema(description = "Estado actual de la cuenta.", example = "OPEN", allowableValues = {"OPEN", "CLOSED", "BLOCKED"})
    private String status;

    @Schema(description = "Saldo actual de la cuenta.", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Moneda de la cuenta.", example = "PEN", allowableValues = {"PEN", "USD"})
    private String currency;

    @Schema(description = "Fecha y hora de apertura de la cuenta.", example = "2026-01-15T10:30:00")
    private LocalDateTime openedAt;

    @Schema(description = "Fecha y hora de cierre de la cuenta. Null si la cuenta está abierta.", example = "2026-06-01T14:00:00", nullable = true)
    private LocalDateTime closedAt;

    @Schema(description = "Identificador del cliente propietario de la cuenta.", example = "CUST001")
    private String customerId;

    @Schema(description = "Lista de titulares para cuentas empresariales.", nullable = true)
    private List<AccountHolderResponse> holders;

    @Schema(description = "Lista de firmantes autorizados para cuentas empresariales.", nullable = true)
    private List<AccountSignerResponse> signers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Titular de cuenta empresarial con porcentaje de propiedad.")
    public static class AccountHolderResponse {
        @Schema(description = "Identificador del titular.", example = "HOLDER001")
        private String holderId;

        @Schema(description = "Porcentaje de propiedad del titular (0-100).", example = "50")
        private Integer percentage;

        @Schema(description = "Indica si es el titular principal.", example = "true")
        private Boolean principal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Firmante autorizado de cuenta empresarial.")
    public static class AccountSignerResponse {
        @Schema(description = "Identificador del firmante.", example = "SIGNER001")
        private String signerId;

        @Schema(description = "Límite máximo de operación autorizado.", example = "10000.00")
        private BigDecimal operationLimit;

        @Schema(description = "Indica si el firmante está activo.", example = "true")
        private Boolean active;
    }

    /**
     * Convierte el resultado del dominio a response.
     */
    public static AccountResponse from(AccountResult result) {
        return AccountResponse.builder()
                .accountId(result.getAccountId())
                .accountNumber(result.getAccountNumber())
                .accountType(result.getAccountType())
                .status(result.getStatus())
                .balance(result.getBalance())
                .currency(result.getCurrency())
                .openedAt(result.getOpenedAt())
                .customerId(result.getCustomerId())
                .holders(result.getHolders() != null ? result.getHolders().stream()
                        .map(h -> AccountHolderResponse.builder()
                                .holderId(h.getHolderId())
                                .percentage(h.getPercentage())
                                .principal(h.getPrincipal())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .signers(result.getSigners() != null ? result.getSigners().stream()
                        .map(s -> AccountSignerResponse.builder()
                                .signerId(s.getSignerId())
                                .operationLimit(s.getOperationLimit())
                                .active(s.getActive())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : null)
                .build();
    }
}