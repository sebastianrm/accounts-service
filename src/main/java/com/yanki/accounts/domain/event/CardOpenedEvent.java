package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento de tarjeta abierta (proveniente de cards-service).
 *
 * <p>Topic: {@code yanki.card.opened.v1}</p>
 *
 * <p>Se consume para actualizar la {@link com.yanki.accounts.domain.model.CustomerCardView}
 * local, marcando si el cliente tiene tarjeta de crédito activa (requisito VIP/PYME).</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardOpenedEvent {

    /** Identificador de la tarjeta. */
    private String cardId;

    /** Identificador del cliente. */
    private String customerId;

    /** Tipo de tarjeta: CREDIT, DEBIT. */
    private String cardType;

    /** Cuenta asociada (solo débito). */
    private String accountId;

    /** Límite de crédito (solo crédito). */
    private BigDecimal creditLimit;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}