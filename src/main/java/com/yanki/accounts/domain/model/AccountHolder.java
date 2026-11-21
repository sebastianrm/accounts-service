package com.yanki.accounts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Titular de una cuenta bancaria empresarial.
 *
 * <p>Las cuentas empresariales pueden tener uno o más titulares,
 * cada uno con un porcentaje de participación. El titular principal
 * ({@link #principal}) tiene autoridad completa sobre la cuenta.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountHolder {

    /** Identificador del titular (customerId). */
    private String holderId;

    /** Porcentaje de titularidad (0-100). */
    private Integer percentage;

    /** Indica si es el titular principal. */
    @Builder.Default
    private Boolean principal = false;
}