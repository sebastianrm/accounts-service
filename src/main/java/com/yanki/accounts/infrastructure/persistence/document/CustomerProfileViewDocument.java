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
 * Documento MongoDB para la vista local del perfil del cliente.
 *
 * <p>Equivalente de persistencia de {@link com.yanki.accounts.domain.model.CustomerProfileView}.
 * Se alimenta con eventos Kafka de customers-service.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer_profile_view")
public class CustomerProfileViewDocument {

    /** Identificador interno del documento. */
    @Id
    private String id;

    /** Identificador del cliente (clave única). */
    private String customerId;

    /** Tipo de cliente: PERSONAL | ENTERPRISE. */
    private String customerType;

    /** Perfil: STANDARD | VIP | PYME. */
    private String profile;

    /** Estado: ACTIVE | BLOCKED | DELETED. */
    private String status;

    /** Tipo de documento. */
    private String documentType;

    /** Número de documento. */
    private String documentNumber;

    /** Email del cliente. */
    private String email;

    /** Última actualización de la vista. */
    private LocalDateTime updatedAt;
}
