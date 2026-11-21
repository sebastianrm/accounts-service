package com.yanki.accounts.domain.port.out;

import com.yanki.accounts.domain.model.Movement;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.time.LocalDateTime;

/**
 * Puerto de salida para la persistencia de movimientos.
 *
 * <p>Define el contrato que el dominio necesita para acceder
 * a los movimientos sin conocer la tecnología de persistencia subyacente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface MovementPersistencePort {

    /**
     * Busca un movimiento por su identificador.
     *
     * @param id identificador del movimiento
     * @return movimiento encontrado o vacío
     */
    Maybe<Movement> findById(String id);

    /**
     * Guarda o actualiza un movimiento.
     *
     * @param movement entidad de dominio a persistir
     * @return movimiento persistido
     */
    Single<Movement> save(Movement movement);

    /**
     * Busca movimientos por cuenta ordenados por fecha descendente.
     *
     * @param accountId identificador de la cuenta
     * @return flujo de movimientos
     */
    Flowable<Movement> findAllByAccountIdOrderByOccurredAtDesc(String accountId);

    /**
     * Busca movimientos por cuenta en un rango de fechas.
     *
     * @param accountId identificador de la cuenta
     * @param from fecha inicio
     * @param to fecha fin
     * @return flujo de movimientos filtrados
     */
    Flowable<Movement> findAllByAccountIdAndOccurredAtBetween(
            String accountId, LocalDateTime from, LocalDateTime to);

    /**
     * Busca movimientos por cliente ordenados por fecha descendente.
     *
     * @param customerId identificador del cliente
     * @return flujo de movimientos
     */
    Flowable<Movement> findAllByCustomerIdOrderByOccurredAtDesc(String customerId);

    /**
     * Cuenta movimientos de una cuenta en un rango de fechas.
     *
     * @param accountId identificador de la cuenta
     * @param from fecha inicio
     * @param to fecha fin
     * @return cantidad de movimientos
     */
    Single<Long> countByAccountIdAndOccurredAtBetween(
            String accountId, LocalDateTime from, LocalDateTime to);

    /**
     * Busca el último movimiento de una cuenta.
     *
     * @param accountId identificador de la cuenta
     * @return último movimiento o vacío
     */
    Maybe<Movement> findFirstByAccountIdOrderByOccurredAtDesc(String accountId);

    /**
     * Busca movimientos de una cuenta en un mes específico (formato yyyy-MM).
     *
     * @param accountId identificador de la cuenta
     * @param month mes en formato yyyy-MM
     * @return flujo de movimientos del mes
     */
    Flowable<Movement> findByAccountIdAndMonth(String accountId, String month);
}
