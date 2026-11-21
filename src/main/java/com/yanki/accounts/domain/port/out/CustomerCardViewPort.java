package com.yanki.accounts.domain.port.out;

import com.yanki.accounts.domain.model.CustomerCardView;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de salida para la vista local de tarjetas del cliente.
 *
 * <p>Define el contrato que el dominio necesita para acceder
 * a la proyección de tarjetas del cliente sin conocer la tecnología subyacente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface CustomerCardViewPort {

    /**
     * Busca la vista de tarjetas por customerId.
     *
     * @param customerId identificador del cliente
     * @return vista encontrada o vacío
     */
    Maybe<CustomerCardView> findByCustomerId(String customerId);

    /**
     * Guarda o actualiza la vista de tarjetas.
     *
     * @param view entidad de dominio a persistir
     * @return vista persistida
     */
    Single<CustomerCardView> save(CustomerCardView view);
}
