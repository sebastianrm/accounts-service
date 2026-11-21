package com.yanki.accounts.domain.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Evento publicado cuando se abre una cuenta bancaria exitosamente.
 *
 * <p>Topic: {@code yanki.account.opened.v1}
 * Key: accountId</p>
 *
 * <p>Se publica tras la creación y persistencia de la cuenta en MongoDB,
 * notificando a otros servicios (reports, notifications) sobre la nueva cuenta.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountOpenedEvent {

    /** Identificador de la cuenta. */
    private String accountId;

    /** Identificador del cliente. */
    private String customerId;

    /** Tipo de producto: SAVINGS, CURRENT, TERM_FIXED. */
    private String productType;

    /** Saldo inicial. */
    private BigDecimal balance;

    /** Comisión de mantenimiento. */
    private BigDecimal maintenanceFee;

    /** Fecha del evento. */
    private LocalDateTime occurredAt;
}