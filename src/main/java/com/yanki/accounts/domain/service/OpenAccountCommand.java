package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Comando para abrir una nueva cuenta bancaria.
 *
 * <p>Encapsula todos los datos necesarios para la operación de apertura de cuenta,
 * incluyendo información del cliente, tipo de producto, monto inicial y, según el tipo,
 * configuración de titulares, firmantes y parámetros de plazo fijo.</p>
 *
 * <p>Los campos condicionales dependen del tipo de cuenta:</p>
 * <ul>
 *   <li>{@code TERM_FIXED}: requiere {@link #termDays} y {@link #allowedMovementDay}</li>
 *   <li>{@code CURRENT} empresarial: requiere {@link #holders} y opcionalmente {@link #signers}</li>
 * </ul>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountCommand {

    /** Identificador del cliente. */
    private String customerId;

    /** Tipo de cuenta: SAVINGS, CURRENT, TERM_FIXED. */
    private String accountType;

    /** Moneda: PEN, USD. */
    private String currency;

    /** Monto inicial de apertura. */
    private BigDecimal initialAmount;

    /** Para TERM_FIXED: días de plazo. */
    private Integer termDays;

    /** Para TERM_FIXED: día del mes permitido (1-28). */
    private Integer allowedMovementDay;

    /** Para CURRENT empresarial: lista de titulares. */
    private List<AccountHolderCommand> holders;

    /** Para CURRENT empresarial: lista de firmantes. */
    private List<AccountSignerCommand> signers;

    /** Canal de apertura. */
    private String channel;

    /** Identificador de referencia externa. */
    private String referenceId;

    /**
     * Comando para titular de cuenta empresarial.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountHolderCommand {
        private String holderId;
        private Integer percentage;
        private Boolean principal;
    }

    /**
     * Comando para firmante autorizado.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSignerCommand {
        private String signerId;
        private BigDecimal operationLimit;
        private Boolean active;
    }
}