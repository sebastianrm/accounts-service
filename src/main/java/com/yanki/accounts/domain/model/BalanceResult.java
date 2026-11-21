package com.yanki.accounts.domain.model;

import java.math.BigDecimal;

/**
 * Resultado de una consulta de saldo que indica el valor y su origen.
 *
 * <p>Permite distinguir si el saldo fue obtenido desde la caché Redis
 * o desde la base de datos MongoDB, para que la capa de presentación
 * pueda informar al cliente si la respuesta fue servida desde caché.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public class BalanceResult {

    private final BigDecimal balance;
    private final boolean cached;

    /**
     * Construye un resultado de saldo desde caché.
     *
     * @param balance El saldo de la cuenta
     * @param cached  {@code true} si el saldo fue obtenido desde la caché Redis
     */
    public BalanceResult(BigDecimal balance, boolean cached) {
        this.balance = balance;
        this.cached = cached;
    }

    /**
     * Retorna el saldo de la cuenta.
     *
     * @return El saldo
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Indica si el saldo fue obtenido desde la caché Redis.
     *
     * @return {@code true} si el saldo provino de la caché
     */
    public boolean isCached() {
        return cached;
    }

    @Override
    public String toString() {
        return "BalanceResult{" +
                "balance=" + balance +
                ", cached=" + cached +
                '}';
    }
}
