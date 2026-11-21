package com.yanki.accounts.infrastructure.persistence.repository;

import com.yanki.accounts.infrastructure.persistence.document.MovementDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Repositorio Spring Data MongoDB reactivo para movimientos.
 *
 * <p>Usa {@link MovementDocument} y métodos derivados, sin {@code @Query}.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SpringDataMovementRepository extends ReactiveMongoRepository<MovementDocument, String> {

    Flux<MovementDocument> findAllByAccountIdOrderByOccurredAtDesc(String accountId);

    Flux<MovementDocument> findAllByAccountIdAndOccurredAtBetween(
            String accountId, LocalDateTime from, LocalDateTime to);

    Flux<MovementDocument> findAllByCustomerIdOrderByOccurredAtDesc(String customerId);

    Mono<Long> countByAccountIdAndOccurredAtBetween(
            String accountId, LocalDateTime from, LocalDateTime to);

    Mono<MovementDocument> findFirstByAccountIdOrderByOccurredAtDesc(String accountId);
}
