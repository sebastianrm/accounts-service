package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.service.AccountResult;
import io.reactivex.rxjava3.core.Flowable;

/**
 * Contrato para estrategias de filtrado de cuentas bancarias.
 *
 * <p>Implementa el patrón de diseño Strategy (GoF) para encapsular algoritmos
 * de búsqueda de cuentas según diferentes combinaciones de filtros opcionales.
 * Cada implementación concreta maneja un subconjunto específico de los tres
 * parámetros de filtrado disponibles: {@code customerId}, {@code type} y {@code status}.</p>
 *
 * <p>El contexto {@link com.yanki.accounts.domain.service.strategy.AccountFilterContext}
 * selecciona la estrategia adecuada en tiempo de ejecución según los criterios
 * proporcionados por el cliente, eliminando la necesidad de condicionales anidados
 * en el código de negocio principal.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * AccountFilterCriteria criteria = AccountFilterCriteria.builder()
 *     .customerId("123")
 *     .type("SAVINGS")
 *     .build();
 * AccountFilterContext context = new AccountFilterContext(repository, mapper);
 * Flowable<AccountResult> results = context.find(criteria);
 * }</pre>
 *
 * @see com.yanki.accounts.domain.service.strategy.AccountFilterContext
 * @see com.yanki.accounts.domain.service.AccountFilterCriteria
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface AccountFilterStrategy {

    /**
     * Ejecuta la búsqueda de cuentas según los criterios encapsulados en esta estrategia.
     *
     * <p>Cada implementación delega en un método específico del repositorio
     * que corresponde a la combinación de filtros que maneja, aplicando
     * el mapeo de {@link com.yanki.accounts.domain.model.Account} a {@link AccountResult}.</p>
     *
     * @return flujo reactivo ({@link Flowable}) con los resultados de las cuentas
     *         que coinciden con los criterios de filtrado
     */
    Flowable<AccountResult> find();
}
