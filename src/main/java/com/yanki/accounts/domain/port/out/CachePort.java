package com.yanki.accounts.domain.port.out;

import io.reactivex.rxjava3.core.Single;

import java.math.BigDecimal;

/**
 * Puerto de salida para la caché de saldos.
 *
 * <p>Define el contrato que el dominio necesita para acceder
 * al saldo en caché sin conocer la tecnología de caché subyacente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface CachePort {

    /**
     * Obtiene el saldo en caché para una cuenta.
     *
     * @param accountId identificador de la cuenta
     * @return saldo en caché o vacío
     */
    Single<BigDecimal> getBalance(String accountId);

    /**
     * Almacena el saldo en caché para una cuenta.
     *
     * @param accountId identificador de la cuenta
     * @param balance saldo a almacenar
     */
    void setBalance(String accountId, BigDecimal balance);

    /**
     * Invalida el saldo en caché para una cuenta.
     *
     * @param accountId identificador de la cuenta
     */
    void invalidateBalance(String accountId);

    /**
     * Incrementa el contador de movimientos mensuales para una cuenta.
     *
     * @param accountId identificador de la cuenta
     * @param month mes en formato yyyy-MM
     * @return nuevo valor del contador
     */
    Single<Integer> incrementMovementCount(String accountId, String month);

    /**
     * Obtiene el contador de movimientos mensuales para una cuenta.
     *
     * @param accountId identificador de la cuenta
     * @param month mes en formato yyyy-MM
     * @return valor actual del contador (0 si no existe)
     */
    Single<Integer> getMovementCount(String accountId, String month);

    /**
     * Limpia los contadores de movimientos de un mes específico.
     *
     * @param month mes en formato yyyy-MM
     */
    void cleanupMovementCounters(String month);
}
