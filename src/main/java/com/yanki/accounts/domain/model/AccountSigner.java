package com.yanki.accounts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Firmante autorizado de una cuenta bancaria empresarial.
 *
 * <p>Las cuentas empresariales pueden tener cero o más firmantes autorizados,
 * cada uno con un límite de operación máximo. Los firmantes pueden realizar
 * transacciones hasta el monto configurado en {@link #operationLimit}.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSigner {

    /** Identificador del firmante (customerId). */
    private String signerId;

    /** Límite máximo por operación para este firmante. */
    private java.math.BigDecimal operationLimit;

    /** Indica si el firmante está activo. */
    @Builder.Default
    private Boolean active = true;
}