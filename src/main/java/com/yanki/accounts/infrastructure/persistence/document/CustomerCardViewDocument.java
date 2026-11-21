package com.yanki.accounts.infrastructure.persistence.document;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB para la vista local de tarjetas del cliente.
 *
 * <p>Equivalente de persistencia de {@link com.yanki.accounts.domain.model.CustomerCardView}.
 * Se alimenta con eventos Kafka de cards-service.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_card_view")
public class CustomerCardViewDocument {

    /** Identificador interno del documento. */
    @Id
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
