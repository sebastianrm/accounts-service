package com.yanki.accounts.domain.service.strategy;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.domain.model.MovementType;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import com.yanki.accounts.domain.service.MovementFilterCriteria;
import io.reactivex.rxjava3.core.Flowable;
import lombok.RequiredArgsConstructor;

/**
 * Contexto que selecciona y ejecuta la estrategia de filtrado adecuada
 * según la combinación de criterios proporcionados para los movimientos.
 *
 * <p>Implementa el patrón de diseño Strategy (GoF) actuando como el contexto
 * que delega la ejecución a la estrategia concreta seleccionada en tiempo de ejecución.
 * Dado un conjunto de filtros opcionales ({@code from}, {@code to}, {@code type}),
 * evalúa qué filtros están presentes y delega en la estrategia correspondiente.</p>
 *
 * <p>Matriz de decisión de selección de estrategias:</p>
 * <table border="1">
 * <caption>Selección de estrategia según combinación de filtros</caption>
 * <tr>
 *   <th>from</th><th>to</th><th>type</th><th>Estrategia seleccionada</th>
 * </tr>
 * <tr>
 *   <td>null</td><td>null</td><td>null</td>
 *   <td>{@link AllMovementsStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>null</td><td>null</td>
 *   <td>{@link ByFromMovementStrategy}</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>presente</td><td>null</td>
 *   <td>{@link ByToMovementStrategy}</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>null</td><td>presente</td>
 *   <td>{@link ByTypeMovementStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>presente</td><td>null</td>
 *   <td>{@link ByFromAndToMovementStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>null</td><td>presente</td>
 *   <td>{@link ByFromAndTypeMovementStrategy}</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>presente</td><td>presente</td>
 *   <td>{@link ByToAndTypeMovementStrategy}</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>presente</td><td>presente</td>
 *   <td>{@link ByFromToAndTypeMovementStrategy}</td>
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
 * @see MovementFilterStrategy
 * @see MovementFilterCriteria
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@RequiredArgsConstructor
public class MovementFilterContext {

    /** Repositorio de movimientos para ejecutar las consultas. */
    private final MovementPersistencePort movementPersistencePort;

    /** Identificador de la cuenta cuyos movimientos se buscan. */
    private final String accountId;

    /**
     * Ejecuta la búsqueda de movimientos utilizando la estrategia
     * que corresponde a la combinación de filtros activa.
     *
     * <p>Este método evalúa los criterios proporcionados, selecciona la estrategia
     * adecuada según la matriz de decisión documentada en la clase, y delega
     * la ejecución a dicha estrategia.</p>
     *
     * @param criteria criterios de filtrado con cualquier combinación de
     *                 {@code from}, {@code to} y {@code type}
     * @return flujo reactivo ({@link Flowable}) con los movimientos
     *         que coinciden con los criterios de filtrado
     */
    public Flowable<Movement> find(MovementFilterCriteria criteria) {
        MovementFilterStrategy strategy = selectStrategy(criteria);
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
    private MovementFilterStrategy selectStrategy(MovementFilterCriteria criteria) {
        boolean hasFrom = criteria.getFrom() != null;
        boolean hasTo = criteria.getTo() != null;
        boolean hasType = criteria.getType() != null;

        MovementType type = hasType ? MovementType.valueOf(criteria.getType()) : null;

        if (hasFrom && hasTo && hasType) {
            return new ByFromToAndTypeMovementStrategy(
                    movementPersistencePort, accountId, criteria.getFrom(), criteria.getTo(), type);
        } else if (hasFrom && hasTo) {
            return new ByFromAndToMovementStrategy(
                    movementPersistencePort, accountId, criteria.getFrom(), criteria.getTo());
        } else if (hasFrom && hasType) {
            return new ByFromAndTypeMovementStrategy(
                    movementPersistencePort, accountId, criteria.getFrom(), type);
        } else if (hasTo && hasType) {
            return new ByToAndTypeMovementStrategy(
                    movementPersistencePort, accountId, criteria.getTo(), type);
        } else if (hasFrom) {
            return new ByFromMovementStrategy(
                    movementPersistencePort, accountId, criteria.getFrom());
        } else if (hasTo) {
            return new ByToMovementStrategy(
                    movementPersistencePort, accountId, criteria.getTo());
        } else if (hasType) {
            return new ByTypeMovementStrategy(
                    movementPersistencePort, accountId, type);
        } else {
            return new AllMovementsStrategy(movementPersistencePort, accountId);
        }
    }
}
