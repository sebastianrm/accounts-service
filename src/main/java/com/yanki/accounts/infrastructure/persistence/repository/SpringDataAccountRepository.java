package com.yanki.accounts.infrastructure.persistence.repository;

import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.infrastructure.persistence.document.AccountDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repositorio Spring Data MongoDB reactivo para cuentas.
 *
 * <p>Usa {@link AccountDocument} y métodos derivados, sin {@code @Query}.
 * Los adaptadores convierten Mono/Flux a RxJava 3 (Single/Maybe/Flowable).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SpringDataAccountRepository extends ReactiveMongoRepository<AccountDocument, String> {

    Flux<AccountDocument> findAllByCustomerId(String customerId);

    Flux<AccountDocument> findAllByCustomerIdAndType(String customerId, String type);

    Flux<AccountDocument> findAllByCustomerIdAndStatus(String customerId, String status);

    Flux<AccountDocument> findAllByCustomerIdAndTypeAndStatus(
            String customerId, String type, String status);

    Flux<AccountDocument> findAllByType(String type);

    Flux<AccountDocument> findAllByStatus(String status);

    Flux<AccountDocument> findAllByTypeAndStatus(String type, String status);

    Mono<Boolean> existsByCustomerIdAndType(String customerId, String type);

    Mono<Long> countByCustomerIdAndType(String customerId, String type);

    Mono<AccountDocument> findByAccountNumber(String accountNumber);
}
