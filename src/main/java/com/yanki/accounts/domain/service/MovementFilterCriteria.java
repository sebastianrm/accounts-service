package com.yanki.accounts.domain.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Criterios de filtrado para movimientos de cuenta.
 *
 * <p>Encapsula los tres parámetros opcionales que puede proporcionar el cliente
 * al consultar los movimientos de una cuenta: {@code from} (fecha inicio),
 * {@code to} (fecha fin) y {@code type} (tipo de movimiento).</p>
 *
 * <p>El contexto {@link com.yanki.accounts.domain.service.strategy.MovementFilterContext}
 * selecciona la estrategia adecuada en tiempo de ejecución según la combinación
 * de filtros presentes en esta instancia.</p>
 *
 * <p>Matriz de combinaciones posibles (8 estrategias):</p>
 * <table border="1">
 * <caption>Combinaciones de filtros</caption>
 * <tr>
 *   <th>from</th><th>to</th><th>type</th><th>Estrategia</th>
 * </tr>
 * <tr>
 *   <td>null</td><td>null</td><td>null</td>
 *   <td>AllMovementsStrategy</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>null</td><td>null</td>
 *   <td>ByFromStrategy</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>presente</td><td>null</td>
 *   <td>ByToStrategy</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>null</td><td>presente</td>
 *   <td>ByTypeStrategy</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>presente</td><td>null</td>
 *   <td>ByFromAndToStrategy</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>null</td><td>presente</td>
 *   <td>ByFromAndTypeStrategy</td>
 * </tr>
 * <tr>
 *   <td>null</td><td>presente</td><td>presente</td>
 *   <td>ByToAndTypeStrategy</td>
 * </tr>
 * <tr>
 *   <td>presente</td><td>presente</td><td>presente</td>
 *   <td>ByFromToAndTypeStrategy</td>
 * </tr>
 * </table>
 *
 * @see com.yanki.accounts.domain.service.strategy.MovementFilterContext
 * @see com.yanki.accounts.domain.service.strategy.MovementFilterStrategy
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovementFilterCriteria {

    /** Fecha de inicio del rango de filtrado (inclusive). */
    private LocalDateTime from;

    /** Fecha de fin del rango de filtrado (inclusive). */
    private LocalDateTime to;

    /** Tipo de movimiento (DEPOSIT, WITHDRAWAL, TRANSFER_IN, etc.). */
    private String type;
}
