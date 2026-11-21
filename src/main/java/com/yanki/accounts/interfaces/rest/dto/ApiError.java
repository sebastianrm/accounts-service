package com.yanki.accounts.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Estructura unificada de respuesta de error para toda la API.
 *
 * <p>Incluye timestamp, status HTTP, descripción del error, código de error,
 * mensaje legible, path del endpoint y detalles opcionales para validaciones.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ApiError", description = "Estructura unificada de respuesta de error para toda la API.", example = "{\"timestamp\":\"2026-11-14T10:30:00\",\"status\":400,\"error\":\"Bad Request\",\"code\":\"INVALID_ACCOUNT_TYPE\",\"message\":\"Tipo de cuenta inválido\",\"path\":\"/api/accounts\"}")
public class ApiError {

    @Schema(description = "Fecha y hora del error.", example = "2026-11-14T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP.", example = "400")
    private int status;

    @Schema(description = "Descripción corta del error.", example = "Bad Request")
    private String error;

    @Schema(description = "Código de error de negocio para identificación programática.", example = "INVALID_ACCOUNT_TYPE")
    private String code;

    @Schema(description = "Mensaje legible del error en español.", example = "Tipo de cuenta inválido")
    private String message;

    @Schema(description = "Ruta del endpoint que generó el error.", example = "/api/accounts")
    private String path;

    @Schema(description = "Detalles adicionales del error, como campos inválidos en validaciones.", nullable = true)
    private Map<String, Object> details;
}
