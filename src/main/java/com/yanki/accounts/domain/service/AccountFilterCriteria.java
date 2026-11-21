package com.yanki.accounts.domain.service;

import lombok.Builder;
import lombok.Getter;

/**
 * Criterios de filtrado para la búsqueda de cuentas bancarias.
 *
 * <p>Agrupa los parámetros opcionales que pueden combinarse de cualquier forma
 * para filtrar el conjunto de cuentas: por cliente, por tipo y por estado.
 * Se utiliza como entrada para el patrón Strategy implementado en
 * {@link com.yanki.accounts.domain.service.strategy.AccountFilterContext}.</p>
 *
 * <p>Los tres filtros disponibles son:</p>
 * <ul>
 *   <li>{@code customerId}: identifica al propietario de las cuentas</li>
 *   <li>{@code type}: tipo de producto (SAVINGS, CURRENT, TERM_FIXED)</li>
 *   <li>{@code status}: estado de la cuenta (OPEN, CLOSED, BLOCKED)</li>
 * </ul>
 *
 * <p>Cualquier combinación de estos filtros genera una estrategia diferente,
 * permitiendo 8 escenarios de búsqueda posibles (2^3 combinaciones).</p>
 *
 * @see com.yanki.accounts.domain.service.strategy.AccountFilterContext
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Builder(toBuilder = true)
public class AccountFilterCriteria {

    /** Identificador del cliente, opcional. */
    private final String customerId;

    /** Tipo de cuenta, opcional (SAVINGS, CURRENT, TERM_FIXED). */
    private final String type;

    /** Estado de la cuenta, opcional (OPEN, CLOSED, BLOCKED). */
    private final String status;

    /**
     * Verifica si no se ha proporcionado ningún filtro.
     *
     * @return {@code true} si todos los filtros son null, {@code false} en caso contrario
     */
    public boolean isEmpty() {
        return customerId == null && type == null && status == null;
    }
}
