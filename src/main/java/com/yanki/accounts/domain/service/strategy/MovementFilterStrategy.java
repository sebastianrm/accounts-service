package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import io.reactivex.rxjava3.core.Flowable;

/**
 * Contrato para estrategias de filtrado de movimientos bancarios.
 *
 * <p>Implementa el patrón de diseño Strategy (GoF) para encapsular algoritmos
 * de búsqueda de movimientos según diferentes combinaciones de filtros opcionales.
 * Cada implementación concreta maneja un subconjunto específico de los tres
 * parámetros de filtrado disponibles: {@code from}, {@code to} y {@code type}.</p>
 *
 * <p>El contexto {@link MovementFilterContext} selecciona la estrategia adecuada
 * en tiempo de ejecución según los criterios proporcionados por el cliente,
 * eliminando la necesidad de condicionales anidados en el código de negocio principal.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * MovementFilterCriteria criteria = MovementFilterCriteria.builder()
 *     .from(LocalDateTime.of(2024, 1, 1, 0, 0))
 *     .type("DEPOSIT")
 *     .build();
 * MovementFilterContext context = new MovementFilterContext(repository, accountId, mapper);
 * Flowable<Movement> results = context.find(criteria);
 * }</pre>
 *
 * @see MovementFilterContext
 * @see com.yanki.accounts.domain.service.MovementFilterCriteria
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface MovementFilterStrategy {

    /**
     * Ejecuta la búsqueda de movimientos según los criterios encapsulados en esta estrategia.
     *
     * <p>Cada implementación delega en un método específico del repositorio
     * que corresponde a la combinación de filtros que maneja, aplicando
     * el mapeo de {@link Movement} al resultado.</p>
     *
     * @return flujo reactivo ({@link Flowable}) con los movimientos
     *         que coinciden con los criterios de filtrado
     */
    Flowable<Movement> find();
}
