package com.yanki.accounts.infrastructure.persistence.repository;

import com.yanki.accounts.infrastructure.persistence.document.CustomerProfileViewDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio Spring Data MongoDB para la vista local del perfil del cliente.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SpringDataCustomerProfileViewRepository
        extends ReactiveMongoRepository<CustomerProfileViewDocument, String> {

    Mono<CustomerProfileViewDocument> findByCustomerId(String customerId);
}
