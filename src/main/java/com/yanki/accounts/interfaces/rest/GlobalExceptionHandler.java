package com.yanki.accounts.interfaces.rest;

import com.yanki.accounts.domain.exception.BusinessException;
import com.yanki.accounts.domain.exception.ErrorCodes;
import com.yanki.accounts.interfaces.rest.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para accounts-service.
 *
 * <p>Intercepta excepciones lanzadas por los controladores REST y las mapea a
 * respuestas HTTP estandarizadas siguiendo el formato {@link ApiError}.
 * Proporciona manejo unificado de errores de negocio, validación y errores internos.</p>
 *
 * <p>Tipos de excepciones manejadas:</p>
 * <ul>
 *   <li>{@link com.yanki.accounts.domain.exception.BusinessException}: Errores de regla de negocio (409/404)</li>
 *   <li>{@link WebExchangeBindException}: Errores de validación de inputs (400)</li>
 *   <li>{@link Exception}: Errores internos no controlados (500)</li>
 * </ul>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusinessException(BusinessException ex, ServerWebExchange exchange) {
        HttpStatus status = mapErrorCodeToStatus(ex.getCode());
        String path = exchange.getRequest().getPath().value();
        log.warn("[EXCEPTION] Error de negocio: code={}, message={}, path={}, status={}",
                ex.getCode(), ex.getMessage(), path, status.value());

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .code(ex.getCode())
                .message(ex.getMessage())
                .path(path)
                .build();

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleValidationException(WebExchangeBindException ex, ServerWebExchange exchange) {
        Map<String, Object> details = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String path = exchange.getRequest().getPath().value();
        log.warn("[EXCEPTION] Error de validación: path={}, fields={}", path, details);

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .code(ErrorCodes.VALIDATION_ERROR)
                .message("Datos de entrada inválidos")
                .path(path)
                .details(details)
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex, ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        log.error("[EXCEPTION] Error interno no controlado: path={}, class={}, message={}",
                path, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        ApiError error = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .code("INTERNAL_ERROR")
                .message("Error interno del servidor")
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

/**
 * Global exception handler for the accounts-service.
 * 
 * <p>Intercepts exceptions thrown by REST controllers and maps them to
 * standardized HTTP error responses following the Yanki platform conventions.</p>
 *
 * <p>Provides unified error response structure including error codes and messages.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
    private HttpStatus mapErrorCodeToStatus(String code) {
        return switch (code) {
            case ErrorCodes.ACCOUNT_NOT_FOUND,
                 ErrorCodes.CUSTOMER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrorCodes.ACCOUNT_ALREADY_CLOSED,
                 ErrorCodes.ACCOUNT_BLOCKED,
                 ErrorCodes.ACCOUNT_INSUFFICIENT_BALANCE,
                 ErrorCodes.ACCOUNT_CANNOT_CLOSE_BALANCE_NOT_ZERO,
                 ErrorCodes.SAVINGS_ACCOUNT_ALREADY_EXISTS,
                 ErrorCodes.CURRENT_ACCOUNT_PERSONAL_LIMIT,
                 ErrorCodes.TERM_FIXED_ACCOUNT_ALREADY_EXISTS,
                 ErrorCodes.ENTERPRISE_CANNOT_HAVE_SAVINGS,
                 ErrorCodes.ENTERPRISE_CANNOT_HAVE_TERM_FIXED,
                 ErrorCodes.MOVEMENT_DAY_NOT_ALLOWED,
                 ErrorCodes.MOVEMENT_ALREADY_DONE_TODAY,
                 ErrorCodes.EXCEEDED_FREE_MOVEMENTS,
                 ErrorCodes.INVALID_MOVEMENT_AMOUNT,
                 ErrorCodes.CREDIT_CARD_REQUIRED_FOR_VIP,
                 ErrorCodes.CREDIT_CARD_REQUIRED_FOR_PYME,
                 ErrorCodes.CUSTOMER_HAS_OVERDUE_DEBT,
                 ErrorCodes.ACCOUNT_BLOCKED_RISK,
                 ErrorCodes.TRANSFER_SAME_ACCOUNT,
                 ErrorCodes.CUSTOMER_INACTIVE,
                 ErrorCodes.INVALID_ACCOUNT_TYPE,
                 ErrorCodes.DEBIT_PAYMENT_REJECTED -> HttpStatus.CONFLICT;
            case ErrorCodes.VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
