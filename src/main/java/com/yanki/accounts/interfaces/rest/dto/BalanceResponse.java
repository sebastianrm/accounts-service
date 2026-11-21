package com.yanki.accounts.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response de consulta de saldo de cuenta.
 *
 * <p>Contiene el saldo actual, moneda e indicador de si el valor fue obtenido
 * desde caché Redis o directamente desde MongoDB.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "BalanceResponse", description = "Respuesta con el saldo disponible de una cuenta bancaria.", example = "{\"accountId\":\"ACC001\",\"balance\":1000.00,\"currency\":\"PEN\",\"cached\":false}")
public class BalanceResponse {

    @Schema(description = "Identificador único de la cuenta.", example = "ACC001")
    private String accountId;

    @Schema(description = "Saldo disponible actual de la cuenta.", example = "1000.00")
    private BigDecimal balance;

    @Schema(description = "Moneda del saldo.", example = "PEN", allowableValues = {"PEN", "USD"})
    private String currency;

    @Schema(description = "Indica si el valor fue obtenido desde caché Redis (true) o directamente desde MongoDB (false).", example = "false")
    private Boolean cached;
}