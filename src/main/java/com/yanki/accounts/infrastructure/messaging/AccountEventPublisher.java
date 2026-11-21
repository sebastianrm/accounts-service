package com.yanki.accounts.infrastructure.messaging;

import com.yanki.accounts.domain.event.AccountClosedEvent;
import com.yanki.accounts.domain.event.AccountMovementRegisteredEvent;
import com.yanki.accounts.domain.event.AccountOpenedEvent;
import com.yanki.accounts.domain.event.DebitPaymentSettledEvent;
import com.yanki.accounts.domain.event.Topics;
import com.yanki.accounts.domain.event.TransferSettledEvent;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicador de eventos de dominio para accounts-service.
 *
 * <p>Publica los eventos definidos en {@link Topics} hacia Kafka.
 * Todos los métodos retornan {@link io.reactivex.rxjava3.core.Completable}
 * para composición reactiva con RxJava.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventPublisher {

    private final KafkaTemplate<String, AccountOpenedEvent> accountOpenedTemplate;
    private final KafkaTemplate<String, AccountClosedEvent> accountClosedTemplate;
    private final KafkaTemplate<String, AccountMovementRegisteredEvent> movementTemplate;
    private final KafkaTemplate<String, DebitPaymentSettledEvent> debitSettledTemplate;
    private final KafkaTemplate<String, TransferSettledEvent> transferSettledTemplate;

    /**
     * Publica evento de apertura de cuenta.
     */
    public Completable publishOpened(AccountOpenedEvent event) {
        return Completable.fromFuture(accountOpenedTemplate.send(Topics.ACCOUNT_OPENED, event.getAccountId(), event))
                .doOnComplete(() -> log.info("Evento {} publicado para cuenta {}", Topics.ACCOUNT_OPENED, event.getAccountId()))
                .onErrorResumeNext(error -> {
                    log.error("Error publicando {}: {}", Topics.ACCOUNT_OPENED, error.getMessage());
                    return Completable.complete();
                });
    }

    /**
     * Publica evento de cierre de cuenta.
     */
    public Completable publishClosed(AccountClosedEvent event) {
        return Completable.fromFuture(accountClosedTemplate.send(Topics.ACCOUNT_CLOSED, event.getAccountId(), event))
                .doOnComplete(() -> log.info("Evento {} publicado para cuenta {}", Topics.ACCOUNT_CLOSED, event.getAccountId()))
                .onErrorResumeNext(error -> {
                    log.error("Error publicando {}: {}", Topics.ACCOUNT_CLOSED, error.getMessage());
                    return Completable.complete();
                });
    }

    /**
     * Publica evento de movimiento registrado.
     */
    public Completable publishMovement(AccountMovementRegisteredEvent event) {
        return Completable.fromFuture(movementTemplate.send(Topics.ACCOUNT_MOVEMENT_REGISTERED, event.getAccountId(), event))
                .doOnComplete(() -> log.debug("Evento {} publicado para cuenta {}", Topics.ACCOUNT_MOVEMENT_REGISTERED, event.getAccountId()))
                .onErrorResumeNext(error -> {
                    log.error("Error publicando {}: {}", Topics.ACCOUNT_MOVEMENT_REGISTERED, error.getMessage());
                    return Completable.complete();
                });
    }

    /**
     * Publica evento de liquidación de pago con tarjeta de débito.
     */
    public Completable publishDebitPaymentSettled(DebitPaymentSettledEvent event) {
        return Completable.fromFuture(debitSettledTemplate.send(Topics.DEBIT_PAYMENT_SETTLED, event.getPaymentId(), event))
                .doOnComplete(() -> log.info("Evento {} publicado para pago {}", Topics.DEBIT_PAYMENT_SETTLED, event.getPaymentId()))
                .onErrorResumeNext(error -> {
                    log.error("Error publicando {}: {}", Topics.DEBIT_PAYMENT_SETTLED, error.getMessage());
                    return Completable.complete();
                });
    }

    /**
     * Publica evento de liquidación de transferencia.
     */
    public Completable publishTransferSettled(TransferSettledEvent event) {
        return Completable.fromFuture(transferSettledTemplate.send(Topics.TRANSFER_SETTLED, event.getTransferId(), event))
                .doOnComplete(() -> log.info("Evento {} publicado para transferencia {}", Topics.TRANSFER_SETTLED, event.getTransferId()))
                .onErrorResumeNext(error -> {
                    log.error("Error publicando {}: {}", Topics.TRANSFER_SETTLED, error.getMessage());
                    return Completable.complete();
                });
    }
}