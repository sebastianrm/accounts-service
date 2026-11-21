package com.yanki.accounts.infrastructure.messaging.adapter;

import com.yanki.accounts.domain.event.AccountClosedEvent;
import com.yanki.accounts.domain.event.AccountMovementRegisteredEvent;
import com.yanki.accounts.domain.event.AccountOpenedEvent;
import com.yanki.accounts.domain.event.DebitPaymentSettledEvent;
import com.yanki.accounts.domain.event.MaintenanceAppliedEvent;
import com.yanki.accounts.domain.event.MaintenanceFailedEvent;
import com.yanki.accounts.domain.event.Topics;
import com.yanki.accounts.domain.event.TransferSettledEvent;
import com.yanki.accounts.domain.event.VipAverageBreachedEvent;
import com.yanki.accounts.domain.port.out.EventPublisherPort;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que publica eventos de dominio hacia Apache Kafka.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventAdapter implements EventPublisherPort {

    private final KafkaTemplate<String, AccountOpenedEvent> accountOpenedTemplate;
    private final KafkaTemplate<String, AccountClosedEvent> accountClosedTemplate;
    private final KafkaTemplate<String, AccountMovementRegisteredEvent> movementTemplate;
    private final KafkaTemplate<String, DebitPaymentSettledEvent> debitSettledTemplate;
    private final KafkaTemplate<String, TransferSettledEvent> transferSettledTemplate;
    private final KafkaTemplate<String, MaintenanceAppliedEvent> maintenanceAppliedTemplate;
    private final KafkaTemplate<String, MaintenanceFailedEvent> maintenanceFailedTemplate;
    private final KafkaTemplate<String, VipAverageBreachedEvent> vipAverageBreachedTemplate;

    @Override
    public Completable publishAccountOpened(AccountOpenedEvent event) {
        log.info("[Kafka] Publicando evento {}: accountId={}", Topics.ACCOUNT_OPENED, event.getAccountId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] AccountOpenedEvent campos -> accountId={}, customerId={}, productType={}, balance={}, maintenanceFee={}, occurredAt={}",
                event.getAccountId(), event.getCustomerId(), event.getProductType(), event.getBalance(), event.getMaintenanceFee(), event.getOccurredAt());
        return Completable.fromFuture(
                accountOpenedTemplate.send(Topics.ACCOUNT_OPENED, event.getAccountId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para cuenta {}", Topics.ACCOUNT_OPENED, event.getAccountId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.ACCOUNT_OPENED, event.getAccountId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishAccountClosed(AccountClosedEvent event) {
        log.info("[Kafka] Publicando evento {}: accountId={}", Topics.ACCOUNT_CLOSED, event.getAccountId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] AccountClosedEvent campos -> accountId={}, occurredAt={}",
                event.getAccountId(), event.getOccurredAt());
        return Completable.fromFuture(
                accountClosedTemplate.send(Topics.ACCOUNT_CLOSED, event.getAccountId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para cuenta {}", Topics.ACCOUNT_CLOSED, event.getAccountId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.ACCOUNT_CLOSED, event.getAccountId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishMovementRegistered(AccountMovementRegisteredEvent event) {
        log.info("[Kafka] Publicando evento {}: accountId={}", Topics.ACCOUNT_MOVEMENT_REGISTERED, event.getAccountId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] AccountMovementRegisteredEvent campos -> movementId={}, accountId={}, customerId={}, movementType={}, amount={}, balanceAfter={}, commission={}, occurredAt={}",
                event.getMovementId(), event.getAccountId(), event.getCustomerId(), event.getMovementType(), event.getAmount(), event.getBalanceAfter(), event.getCommission(), event.getOccurredAt());
        return Completable.fromFuture(
                movementTemplate.send(Topics.ACCOUNT_MOVEMENT_REGISTERED, event.getAccountId(), event)
        ).doOnComplete(() -> log.debug("[Kafka] Evento {} publicado para cuenta {}", Topics.ACCOUNT_MOVEMENT_REGISTERED, event.getAccountId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.ACCOUNT_MOVEMENT_REGISTERED, event.getAccountId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishDebitPaymentSettled(DebitPaymentSettledEvent event) {
        log.info("[Kafka] Publicando evento {}: paymentId={}", Topics.DEBIT_PAYMENT_SETTLED, event.getPaymentId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] DebitPaymentSettledEvent campos -> paymentId={}, cardId={}, status={}, reason={}, occurredAt={}",
                event.getPaymentId(), event.getCardId(), event.getStatus(), event.getReason(), event.getOccurredAt());
        return Completable.fromFuture(
                debitSettledTemplate.send(Topics.DEBIT_PAYMENT_SETTLED, event.getPaymentId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para pago {}", Topics.DEBIT_PAYMENT_SETTLED, event.getPaymentId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.DEBIT_PAYMENT_SETTLED, event.getPaymentId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishTransferSettled(TransferSettledEvent event) {
        log.info("[Kafka] Publicando evento {}: transferId={}", Topics.TRANSFER_SETTLED, event.getTransferId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] TransferSettledEvent campos -> transferId={}, status={}, sourceAccountId={}, reason={}, occurredAt={}",
                event.getTransferId(), event.getStatus(), event.getSourceAccountId(), event.getReason(), event.getOccurredAt());
        return Completable.fromFuture(
                transferSettledTemplate.send(Topics.TRANSFER_SETTLED, event.getTransferId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para transferencia {}", Topics.TRANSFER_SETTLED, event.getTransferId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.TRANSFER_SETTLED, event.getTransferId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishMaintenanceApplied(MaintenanceAppliedEvent event) {
        log.info("[Kafka] Publicando evento {}: accountId={}", Topics.ACCOUNT_MAINTENANCE_APPLIED, event.getAccountId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] MaintenanceAppliedEvent campos -> accountId={}, customerId={}, commissionAmount={}, balanceAfter={}, month={}, occurredAt={}",
                event.getAccountId(), event.getCustomerId(), event.getCommissionAmount(), event.getBalanceAfter(), event.getMonth(), event.getOccurredAt());
        return Completable.fromFuture(
                maintenanceAppliedTemplate.send(Topics.ACCOUNT_MAINTENANCE_APPLIED, event.getAccountId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para cuenta {}", Topics.ACCOUNT_MAINTENANCE_APPLIED, event.getAccountId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.ACCOUNT_MAINTENANCE_APPLIED, event.getAccountId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishMaintenanceFailed(MaintenanceFailedEvent event) {
        log.info("[Kafka] Publicando evento {}: accountId={}", Topics.ACCOUNT_MAINTENANCE_FAILED, event.getAccountId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] MaintenanceFailedEvent campos -> accountId={}, customerId={}, commissionAmount={}, currentBalance={}, month={}, reason={}, occurredAt={}",
                event.getAccountId(), event.getCustomerId(), event.getCommissionAmount(), event.getCurrentBalance(), event.getMonth(), event.getReason(), event.getOccurredAt());
        return Completable.fromFuture(
                maintenanceFailedTemplate.send(Topics.ACCOUNT_MAINTENANCE_FAILED, event.getAccountId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para cuenta {}", Topics.ACCOUNT_MAINTENANCE_FAILED, event.getAccountId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.ACCOUNT_MAINTENANCE_FAILED, event.getAccountId(), error.getMessage());
             return Completable.complete();
         });
    }

    @Override
    public Completable publishVipAverageBreached(VipAverageBreachedEvent event) {
        log.info("[Kafka] Publicando evento {}: accountId={}", Topics.VIP_AVERAGE_BREACHED, event.getAccountId());
        log.debug("[Kafka] Clase del evento publicado: {}", event.getClass().getName());
        log.debug("[Kafka] VipAverageBreachedEvent campos -> accountId={}, customerId={}, dailyAverage={}, minRequiredAverage={}, penaltyAmount={}, balanceAfter={}, month={}, occurredAt={}",
                event.getAccountId(), event.getCustomerId(), event.getDailyAverage(), event.getMinRequiredAverage(), event.getPenaltyAmount(), event.getBalanceAfter(), event.getMonth(), event.getOccurredAt());
        return Completable.fromFuture(
                vipAverageBreachedTemplate.send(Topics.VIP_AVERAGE_BREACHED, event.getAccountId(), event)
        ).doOnComplete(() -> log.info("[Kafka] Evento {} publicado para cuenta {}", Topics.VIP_AVERAGE_BREACHED, event.getAccountId()))
         .onErrorResumeNext(error -> {
             log.error("[Kafka] Error publicando {}: {} - {}", Topics.VIP_AVERAGE_BREACHED, event.getAccountId(), error.getMessage());
             return Completable.complete();
         });
    }
}
