package com.yanki.accounts.infrastructure.persistence.repository;

import com.yanki.accounts.infrastructure.persistence.document.CustomerRiskViewDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio Spring Data MongoDB para la vista local de riesgo del cliente.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface SpringDataCustomerRiskViewRepository
        extends ReactiveMongoRepository<CustomerRiskViewDocument, String> {

    Mono<CustomerRiskViewDocument> findByCustomerId(String customerId);
}
