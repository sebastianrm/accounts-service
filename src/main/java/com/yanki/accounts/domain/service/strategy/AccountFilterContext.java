package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountStatus;
import com.yanki.accounts.domain.model.AccountType;
import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.service.AccountFilterCriteria;
import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;

/**
 * Contexto que selecciona y ejecuta la estrategia de filtrado adecuada
 * según la combinación de criterios proporcionados.
 *
 * <p>Implementa el patrón de diseño Strategy (GoF) actuando como el contexto
 * que delega la ejecución a la estrategia concreta seleccionada en tiempo de ejecución.
 * Dado un conjunto de filtros opcionales ({@code customerId}, {@code type}, {@code status}),
 * evalúa qué filtros están presentes y delega en la estrategia correspondiente.</p>
 *
 * <p>Matriz de decisión de selección de estrategias:</p>
 * <table border="1">
 * <caption>Selección de estrategia según combinación de filtros</caption>
 * <tr>
 *   <th>customerId</th><th>type</th><th>status</th><th>Estrategia seleccionada</th>
 * </tr>
 * <tr>
 *   <td>null</td><td>null</td><td>null</td>
 *   <td>{@link AllAccountsStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>null</td><td>null</td>
 *   <td>{@link ByCustomerStrategy}</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>presente</td><td>null</td>
 *   <td>{@link ByTypeStrategy}</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>null</td><td>presente</td>
 *   <td>{@link ByStatusStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>presente</td><td>null</td>
 *   <td>{@link ByCustomerAndTypeStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>null</td><td>presente</td>
 *   <td>{@link ByCustomerAndStatusStrategy}</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>presente</td><td>presente</td>
 *   <td>{@link ByTypeAndStatusStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>presente</td><td>presente</td>
 *   <td>{@link ByCustomerTypeAndStatusStrategy}</td>
 * </tr>
 * </table>
 *
 * <p>Beneficios del patrón Strategy:</p>
 * <ul>
 *   <li>Elimina condicionales anidados en el código de negocio</li>
 *   <li>Cada estrategia es independiente y testeable</li>
 *   <li>Nuevas combinaciones de filtros se agregan como nuevas estrategias</li>
 *   <li>El contexto mantiene solo la lógica de selección</li>
 * </ul>
 *
 * @see AccountFilterStrategy
 * @see AccountFilterCriteria
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class AccountFilterContext {

    /** Puerto de persistencia de cuentas para ejecutar las consultas. */
    private final AccountPersistencePort accountPersistencePort;

    /** Función de mapeo de Account a AccountResult. */
    private final java.util.function.Function<Account, AccountResult> mapper;

    /**
     * Ejecuta la búsqueda de cuentas utilizando la estrategia
     * que corresponde a la combinación de filtros activa.
     *
     * <p>Este método evalúa los criterios proporcionados, selecciona la estrategia
     * adecuada según la matriz de decisión documentada en la clase, y delega
     * la ejecución a dicha estrategia.</p>
     *
     * @param criteria criterios de filtrado con cualquier combinación de
     *                 {@code customerId}, {@code type} y {@code status}
     * @return flujo reactivo ({@link Flowable}) con los resultados de las cuentas
     *         que coinciden con los criterios de filtrado
     */
    public Flowable<AccountResult> find(AccountFilterCriteria criteria) {
        AccountFilterStrategy strategy = selectStrategy(criteria);
        return strategy.find();
    }

    /**
     * Selecciona la estrategia concreta según la combinación de filtros presentes.
     *
     * <p>Evalúa cada parámetro de los criterios y construye la estrategia
     * correspondiente según la matriz de decisión documentada en la clase.</p>
     *
     * @param criteria criterios de filtrado a evaluar
     * @return estrategia seleccionada para ejecutar la búsqueda
     */
    private AccountFilterStrategy selectStrategy(AccountFilterCriteria criteria) {
        boolean hasCustomer = criteria.getCustomerId() != null;
        boolean hasType = criteria.getType() != null;
        boolean hasStatus = criteria.getStatus() != null;

        AccountType type = hasType ? AccountType.valueOf(criteria.getType()) : null;
        AccountStatus status = hasStatus ? AccountStatus.valueOf(criteria.getStatus()) : null;

        if (hasCustomer && hasType && hasStatus) {
            return new ByCustomerTypeAndStatusStrategy(
                    accountPersistencePort, criteria.getCustomerId(), type, status, mapper);
        } else if (hasCustomer && hasType) {
            return new ByCustomerAndTypeStrategy(
                    accountPersistencePort, criteria.getCustomerId(), type, mapper);
        } else if (hasCustomer && hasStatus) {
            return new ByCustomerAndStatusStrategy(
                    accountPersistencePort, criteria.getCustomerId(), status, mapper);
        } else if (hasType && hasStatus) {
            return new ByTypeAndStatusStrategy(
                    accountPersistencePort, type, status, mapper);
        } else if (hasCustomer) {
            return new ByCustomerStrategy(
                    accountPersistencePort, criteria.getCustomerId(), mapper);
        } else if (hasType) {
            return new ByTypeStrategy(
                    accountPersistencePort, type, mapper);
        } else if (hasStatus) {
            return new ByStatusStrategy(
                    accountPersistencePort, status, mapper);
        } else {
            return new AllAccountsStrategy(accountPersistencePort, mapper);
        }
    }
}
