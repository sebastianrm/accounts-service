package com.yanki.accounts.infrastructure.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio para eventos procesados (idempotencia).
 *
 * <p>Almacena las claves de idempotencia {@code <topic>:<entityId>:<occurredAt>}
 * para evitar el procesamiento duplicado de eventos Kafka.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface ProcessedEventRepository extends ReactiveMongoRepository<ProcessedEvent, String> {

    /**
     * Busca por clave de idempotencia.
     */
    Mono<ProcessedEvent> findById(String id);
}