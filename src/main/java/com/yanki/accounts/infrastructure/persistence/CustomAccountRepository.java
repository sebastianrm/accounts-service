package com.yanki.accounts.infrastructure.persistence;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Interfaz para consultas personalizadas de cuenta.
 *
 * <p>Define métodos que requieren lógica de consulta más compleja
 * que los métodos derivados de Spring Data MongoDB.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface CustomAccountRepository {

    Flux<Account> findByCustomerAndTypeAndStatus(String customerId, AccountType type, com.yanki.accounts.domain.model.AccountStatus status);

    Mono<Long> countMovementsInMonth(String accountId, String yearMonth);

    Flux<Movement> findMovementsByAccountAndDateRange(String accountId, LocalDateTime from, LocalDateTime to);
}