package com.yanki.accounts.infrastructure.persistence.adapter;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.infrastructure.persistence.document.AccountDocument;
import com.yanki.accounts.infrastructure.persistence.mapper.AccountDocumentMapper;
import com.yanki.accounts.infrastructure.persistence.repository.SpringDataAccountRepository;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


/**
 * Adaptador de salida que implementa la persistencia de cuentas sobre MongoDB Reactivo.
 *
 * <p>Convierte los tipos de Project Reactor (Mono/Flux) a RxJava 3 (Single/Maybe/Flowable)
 * y traduce entre {@link Account} (dominio) y {@link com.yanki.accounts.infrastructure.persistence.document.AccountDocument} (persistencia).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountMongoAdapter implements AccountPersistencePort {

    private final SpringDataAccountRepository repository;

    @Override
    public Maybe<Account> findById(String id) {
        log.debug("[MongoDB] Buscando cuenta por ID: {}", id);
        return Maybe.fromPublisher(
                repository.findById(id).map(AccountDocumentMapper::toDomain)
        ).doOnSuccess(account -> log.debug("[MongoDB] Cuenta encontrada: id={}", id))
         .doOnError(throwable -> log.error("[MongoDB] Error buscando cuenta por ID: {} - {}", id, throwable.getMessage()));
    }

    @Override
    public Single<Account> save(Account account) {
        log.debug("[MongoDB] Guardando cuenta: id={}, type={}, balance={}", account.getId(), account.getType(), account.getBalance());
        AccountDocument document = AccountDocumentMapper.toDocument(account);
        Mono<AccountDocument> saveMono;
        if (account.getId() != null && !account.getId().isEmpty()) {
            saveMono = repository.findById(account.getId())
                    .switchIfEmpty(Mono.just(document))
                    .map(existing -> {
                        document.setVersion(existing.getVersion());
                        return document;
                    })
                    .flatMap(repository::save);
        } else {
            saveMono = repository.save(document);
        }
        return Single.fromPublisher(
                saveMono.map(AccountDocumentMapper::toDomain)
        ).doOnSuccess(saved -> log.debug("[MongoDB] Cuenta guardada: id={}", saved.getId()))
         .doOnError(throwable -> log.error("[MongoDB] Error guardando cuenta: id={} - {}", account.getId(), throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByCustomerId(String customerId) {
        log.debug("[MongoDB] Buscando cuentas por customerId: {}", customerId);
        return Flowable.fromPublisher(
                repository.findAllByCustomerId(customerId)
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas por customerId: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByCustomerIdAndType(String customerId, AccountType type) {
        log.debug("[MongoDB] Buscando cuentas por customerId: {}, type: {}", customerId, type);
        return Flowable.fromPublisher(
                repository.findAllByCustomerIdAndType(customerId, type.name())
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas por customerId y type: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByCustomerIdAndStatus(String customerId, AccountStatus status) {
        log.debug("[MongoDB] Buscando cuentas por customerId: {}, status: {}", customerId, status);
        return Flowable.fromPublisher(
                repository.findAllByCustomerIdAndStatus(customerId, status.name())
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas por customerId y status: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByCustomerIdAndTypeAndStatus(
            String customerId, AccountType type, AccountStatus status) {
        log.debug("[MongoDB] Buscando cuentas por customerId: {}, type: {}, status: {}", customerId, type, status);
        return Flowable.fromPublisher(
                repository.findAllByCustomerIdAndTypeAndStatus(customerId, type.name(), status.name())
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas con filtros: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByType(AccountType type) {
        log.debug("[MongoDB] Buscando cuentas por type: {}", type);
        return Flowable.fromPublisher(
                repository.findAllByType(type.name())
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas por type: {} - {}", type, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByStatus(AccountStatus status) {
        log.debug("[MongoDB] Buscando cuentas por status: {}", status);
        return Flowable.fromPublisher(
                repository.findAllByStatus(status.name())
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas por status: {} - {}", status, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAllByTypeAndStatus(AccountType type, AccountStatus status) {
        log.debug("[MongoDB] Buscando cuentas por type: {}, status: {}", type, status);
        return Flowable.fromPublisher(
                repository.findAllByTypeAndStatus(type.name(), status.name())
                        .map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuentas por type y status: {} - {}", type, throwable.getMessage()));
    }

    @Override
    public Single<Boolean> existsByCustomerIdAndType(String customerId, AccountType type) {
        log.debug("[MongoDB] Verificando existencia de cuenta: customerId={}, type={}", customerId, type);
        return Single.fromPublisher(
                repository.existsByCustomerIdAndType(customerId, type.name())
        ).doOnError(throwable -> log.error("[MongoDB] Error verificando existencia de cuenta: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Single<Long> countByCustomerIdAndType(String customerId, AccountType type) {
        log.debug("[MongoDB] Contando cuentas: customerId={}, type={}", customerId, type);
        return Single.fromPublisher(
                repository.countByCustomerIdAndType(customerId, type.name())
        ).doOnError(throwable -> log.error("[MongoDB] Error contando cuentas: {} - {}", customerId, throwable.getMessage()));
    }

    @Override
    public Maybe<Account> findByAccountNumber(String accountNumber) {
        log.debug("[MongoDB] Buscando cuenta por accountNumber: {}", accountNumber);
        return Maybe.fromPublisher(
                repository.findByAccountNumber(accountNumber).map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error buscando cuenta por accountNumber: {} - {}", accountNumber, throwable.getMessage()));
    }

    @Override
    public Flowable<Account> findAll() {
        log.debug("[MongoDB] Listando todas las cuentas");
        return Flowable.fromPublisher(
                repository.findAll().map(AccountDocumentMapper::toDomain)
        ).doOnError(throwable -> log.error("[MongoDB] Error listando todas las cuentas - {}", throwable.getMessage()));
    }
}
