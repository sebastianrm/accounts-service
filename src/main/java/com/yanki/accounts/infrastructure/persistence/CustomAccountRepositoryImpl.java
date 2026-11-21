package com.yanki.accounts.infrastructure.persistence;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.Movement;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Implementación personalizada de repositorio para consultas complejas.
 *
 * <p>Usa {@link ReactiveMongoTemplate} con Aggregation Framework
 * cuando los métodos derivados no son suficientes.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Repository
public class CustomAccountRepositoryImpl implements CustomAccountRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public CustomAccountRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Account> findByCustomerAndTypeAndStatus(String customerId, AccountType type, com.yanki.accounts.domain.model.AccountStatus status) {
        Query query = Query.query(Criteria.where("customerId").is(customerId)
                .and("type").is(type)
                .and("status").is(status));
        return mongoTemplate.find(query, Account.class);
    }

    @Override
    public Mono<Long> countMovementsInMonth(String accountId, String yearMonth) {
        Query query = Query.query(Criteria.where("accountId").is(accountId)
                .and("currentMonth").is(yearMonth));
        return mongoTemplate.count(query, Account.class);
    }

    @Override
    public Flux<Movement> findMovementsByAccountAndDateRange(String accountId, LocalDateTime from, LocalDateTime to) {
        Query query = Query.query(Criteria.where("accountId").is(accountId)
                .and("occurredAt").gte(from).lte(to));
        return mongoTemplate.find(query, Movement.class);
    }
}