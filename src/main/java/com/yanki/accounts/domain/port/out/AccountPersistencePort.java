package com.yanki.accounts.domain.port.out;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de salida para la persistencia de cuentas.
 *
 * <p>Define el contrato que el dominio necesita para acceder
 * a las cuentas sin conocer la tecnología de persistencia subyacente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface AccountPersistencePort {

    /**
     * Busca una cuenta por su identificador.
     *
     * @param id identificador de la cuenta
     * @return cuenta encontrada o vacío
     */
    Maybe<Account> findById(String id);

    /**
     * Guarda o actualiza una cuenta en el almacenamiento.
     *
     * @param account entidad de dominio a persistir
     * @return cuenta persistida
     */
    Single<Account> save(Account account);

    /**
     * Busca cuentas por cliente.
     *
     * @param customerId identificador del cliente
     * @return flujo de cuentas del cliente
     */
    Flowable<Account> findAllByCustomerId(String customerId);

    /**
     * Busca cuentas por cliente y tipo.
     *
     * @param customerId identificador del cliente
     * @param type tipo de cuenta
     * @return flujo de cuentas filtradas
     */
    Flowable<Account> findAllByCustomerIdAndType(String customerId, AccountType type);

    /**
     * Busca cuentas por cliente y estado.
     *
     * @param customerId identificador del cliente
     * @param status estado de la cuenta
     * @return flujo de cuentas filtradas
     */
    Flowable<Account> findAllByCustomerIdAndStatus(String customerId, AccountStatus status);

    /**
     * Busca cuentas por cliente, tipo y estado.
     *
     * @param customerId identificador del cliente
     * @param type tipo de cuenta
     * @param status estado de la cuenta
     * @return flujo de cuentas filtradas
     */
    Flowable<Account> findAllByCustomerIdAndTypeAndStatus(
            String customerId, AccountType type, AccountStatus status);

    /**
     * Busca cuentas por tipo.
     *
     * @param type tipo de cuenta
     * @return flujo de cuentas del tipo
     */
    Flowable<Account> findAllByType(AccountType type);

    /**
     * Busca cuentas por estado.
     *
     * @param status estado de la cuenta
     * @return flujo de cuentas del estado
     */
    Flowable<Account> findAllByStatus(AccountStatus status);

    /**
     * Busca cuentas por tipo y estado.
     *
     * @param type tipo de cuenta
     * @param status estado de la cuenta
     * @return flujo de cuentas filtradas
     */
    Flowable<Account> findAllByTypeAndStatus(AccountType type, AccountStatus status);

    /**
     * Verifica si existe una cuenta de un tipo para el cliente.
     *
     * @param customerId identificador del cliente
     * @param type tipo de cuenta
     * @return true si existe
     */
    Single<Boolean> existsByCustomerIdAndType(String customerId, AccountType type);

    /**
     * Cuenta cuentas de un tipo para un cliente.
     *
     * @param customerId identificador del cliente
     * @param type tipo de cuenta
     * @return cantidad de cuentas
     */
    Single<Long> countByCustomerIdAndType(String customerId, AccountType type);

    /**
     * Busca cuenta por número de cuenta.
     *
     * @param accountNumber número de cuenta
     * @return cuenta encontrada o vacío
     */
    Maybe<Account> findByAccountNumber(String accountNumber);

    /**
     * Lista todas las cuentas.
     *
     * @return flujo de todas las cuentas
     */
    Flowable<Account> findAll();
}
