package com.yanki.accounts.domain.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de cliente eliminado (proveniente de customers-service).
 *
 * <p>Topic: {@code yanki.customer.deleted.v1}</p>
 *
 * <p>Se consume para marcar las vistas locales del cliente como DELETED,
 * impidiendo nuevas operaciones sobre sus cuentas.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDeletedEvent {

    /** Identificador del cliente. */
    private String customerId;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}