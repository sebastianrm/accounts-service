package com.yanki.accounts.domain.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vista local del perfil del cliente (proyección desde eventos Kafka).
 *
 * <p>Se alimenta con los eventos {@code yanki.customer.registered.v1},
 * {@code yanki.customer.updated.v1} y {@code yanki.customer.deleted.v1}.
 * Permite conocer el tipo y perfil del cliente al abrir una cuenta
 * sin consultar el servicio de clientes por REST.</p>
 *
 * <p>Esta vista es la fuente de verdad para decisiones de negocio como:
 * validación de límites por tipo de cliente, requisito de tarjeta crédito
 * para perfiles VIP/PYME y verificación de estado activo.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileView {

    /** Identificador interno del documento. */
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

    /**
     * Verifica si el cliente puede operar.
     *
     * @return true si está ACTIVE y no DELETED
     */
    public boolean canOperate() {
        return "ACTIVE".equals(this.status) && !"DELETED".equals(this.status);
    }

    /**
     * Verifica si es cliente personal.
     */
    public boolean isPersonal() {
        return "PERSONAL".equals(this.customerType);
    }

    /**
     * Verifica si es cliente empresarial.
     */
    public boolean isEnterprise() {
        return "ENTERPRISE".equals(this.customerType);
    }

    /**
     * Verifica si tiene perfil VIP.
     */
    public boolean isVip() {
        return "VIP".equals(this.profile);
    }

    /**
     * Verifica si tiene perfil PYME.
     */
    public boolean isPyme() {
        return "PYME".equals(this.profile);
    }
}
