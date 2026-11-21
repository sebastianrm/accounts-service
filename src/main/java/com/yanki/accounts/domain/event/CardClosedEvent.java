package com.yanki.accounts.domain.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de tarjeta cerrada (proveniente de cards-service).
 *
 * <p>Topic: {@code yanki.card.closed.v1}</p>
 *
 * <p>Se consume para actualizar la {@link com.yanki.accounts.domain.model.CustomerCardView}
 * local, desmarcando la tarjeta de crédito activa del cliente.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardClosedEvent {

    /** Identificador de la tarjeta. */
    private String cardId;

    /** Identificador del cliente. */
    private String customerId;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}