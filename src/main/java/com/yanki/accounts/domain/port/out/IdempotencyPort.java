package com.yanki.accounts.domain.port.out;

import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de salida para el control de idempotencia de eventos Kafka.
 *
 * <p>Define el contrato que el dominio necesita para verificar
 * y registrar eventos procesados sin conocer la tecnología subyacente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface IdempotencyPort {

    /**
     * Verifica si un evento ya fue procesado y lo marca como procesado si no lo estaba.
     *
     * @param idempotencyKey clave de idempotencia (topic:entityId:occurredAt)
     * @return true si ya estaba procesado, false si es nuevo
     */
    Single<Boolean> checkAndMarkProcessed(String idempotencyKey);
}
