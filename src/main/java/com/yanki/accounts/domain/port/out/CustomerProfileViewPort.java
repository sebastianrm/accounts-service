package com.yanki.accounts.domain.port.out;

import com.yanki.accounts.domain.model.CustomerProfileView;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de salida para la vista local del perfil del cliente.
 *
 * <p>Define el contrato que el dominio necesita para acceder
 * a la proyección del perfil del cliente sin conocer la tecnología subyacente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public interface CustomerProfileViewPort {

    /**
     * Busca la vista de perfil por customerId.
     *
     * @param customerId identificador del cliente
     * @return vista encontrada o vacío
     */
    Maybe<CustomerProfileView> findByCustomerId(String customerId);

    /**
     * Guarda o actualiza la vista de perfil.
     *
     * @param view entidad de dominio a persistir
     * @return vista persistida
     */
    Single<CustomerProfileView> save(CustomerProfileView view);
}
