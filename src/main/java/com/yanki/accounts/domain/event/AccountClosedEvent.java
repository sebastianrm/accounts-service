package com.yanki.accounts.domain.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento publicado cuando se cierra una cuenta bancaria exitosamente.
 *
 * <p>Topic: {@code yanki.account.closed.v1}
 * Key: accountId</p>
 *
 * <p>Se publica tras el cierre definitivo de la cuenta (saldo debe ser 0),
 * notificando a otros servicios sobre la cuenta cerrada.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountClosedEvent {

    /** Identificador de la cuenta. */
    private String accountId;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}