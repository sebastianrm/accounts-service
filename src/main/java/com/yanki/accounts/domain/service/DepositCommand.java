package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Comando para realizar un depósito en una cuenta bancaria.
 *
 * <p>Encapsula los datos necesarios para acreditar un monto a una cuenta,
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
public class DepositCommand {

    /** Identificador de la cuenta. */
    private String accountId;

    /** Importe a depositar. */
    private BigDecimal amount;

    /** Descripción del depósito. */
    private String description;

    /** Canal: BRANCH, ATM, MOBILE, WEB. */
    private String channel;

    /** Identificador de referencia externa. */
    private String referenceId;
}