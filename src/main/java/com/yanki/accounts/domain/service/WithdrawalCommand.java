package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Comando para realizar un retiro de una cuenta bancaria.
 *
 * <p>Encapsula los datos necesarios para debitar un monto de una cuenta,
 * incluyendo el importe, descripción, canal de origen y referencia externa.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalCommand {

    /** Identificador de la cuenta. */
    private String accountId;

    /** Importe a retirar. */
    private BigDecimal amount;

    /** Descripción del retiro. */
    private String description;

    /** Canal: BRANCH, ATM, MOBILE, WEB. */
    private String channel;

    /** Identificador de referencia externa. */
    private String referenceId;
}