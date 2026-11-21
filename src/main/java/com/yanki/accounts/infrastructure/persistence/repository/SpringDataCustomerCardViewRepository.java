package com.yanki.accounts.infrastructure.persistence.repository;

import com.yanki.accounts.infrastructure.persistence.document.CustomerCardViewDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio Spring Data MongoDB para la vista local de tarjetas del cliente.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SpringDataCustomerCardViewRepository
        extends ReactiveMongoRepository<CustomerCardViewDocument, String> {

    Mono<CustomerCardViewDocument> findByCustomerId(String customerId);
}
