package com.yanki.accounts.domain.event;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de cliente registrado (proveniente de customers-service).
 *
 * <p>Topic: {@code yanki.customer.registered.v1}</p>
 *
 * <p>Se consume para crear la {@link com.yanki.accounts.domain.model.CustomerProfileView}
 * local y permitir la apertura de cuentas sin consultar customers-service por REST.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisteredEvent {

    /** Identificador del cliente. */
    private String customerId;

    /** Username (email en minúsculas). */
    private String username;

    /** Email del cliente. */
    private String email;

    /** Roles asignados. */
    private List<String> roles;

    /** Tipo de cliente: PERSONAL, ENTERPRISE. */
    private String customerType;

    /** Tipo de documento. */
    private String documentType;

    /** Número de documento. */
    private String documentNumber;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}