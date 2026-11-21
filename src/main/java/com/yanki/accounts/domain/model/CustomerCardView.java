package com.yanki.accounts.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vista local de tarjetas del cliente (proyección desde eventos Kafka).
 *
 * <p>Se alimenta con los eventos {@code yanki.card.opened.v1} y {@code yanki.card.closed.v1}.
 * Permite verificar si el cliente ya tiene tarjeta de crédito, requisito
 * obligatorio para abrir cuentas VIP (PERSONAL) o PYME (ENTERPRISE).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCardView {

    /** Identificador interno del documento. */
    private String id;

    /** Identificador del cliente (clave única). */
    private String customerId;

    /** Identificador de la tarjeta de crédito activa. */
    private String creditCardId;

    /** Indica si el cliente tiene al menos una tarjeta de crédito activa. */
    @Builder.Default
    private Boolean hasCreditCard = false;

    /** Última actualización de la vista. */
    private LocalDateTime updatedAt;
}
