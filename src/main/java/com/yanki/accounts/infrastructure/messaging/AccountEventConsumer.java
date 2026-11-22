package com.yanki.accounts.infrastructure.messaging;

import com.yanki.accounts.domain.service.AccountService;
import com.yanki.accounts.domain.event.CardOpenedEvent;
import com.yanki.accounts.domain.event.CardClosedEvent;
import com.yanki.accounts.domain.event.CreditOverdueEvent;
import com.yanki.accounts.domain.event.CustomerRegisteredEvent;
import com.yanki.accounts.domain.event.CustomerUpdatedEvent;
import com.yanki.accounts.domain.event.CustomerDeletedEvent;
import com.yanki.accounts.domain.event.DebitPaymentRequestedEvent;
import com.yanki.accounts.domain.event.Topics;
import com.yanki.accounts.domain.event.TransferRequestedEvent;
import com.yanki.accounts.domain.model.CustomerCardView;
import com.yanki.accounts.domain.model.CustomerProfileView;
import com.yanki.accounts.domain.model.CustomerRiskView;
import com.yanki.accounts.domain.port.out.CustomerCardViewPort;
import com.yanki.accounts.domain.port.out.CustomerProfileViewPort;
import com.yanki.accounts.domain.port.out.CustomerRiskViewPort;
import com.yanki.accounts.domain.port.out.IdempotencyPort;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de dominio para accounts-service.
 *
 * <p>Consume eventos de otros servicios y actualiza las vistas locales
 * o ejecuta lógica de negocio (liquidación de débitos, transferencias).</p>
 *
 * <p>Todos los consumidores son <b>idempotentes</b>: usan la clave
 * {@code <topic>:<entityId>:<occurredAt>} guardada en {@code processed_events}.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventConsumer {

    private final AccountService accountService;
    private final IdempotencyPort idempotencyPort;
    private final CustomerProfileViewPort profileViewPort;
    private final CustomerCardViewPort cardViewPort;
    private final CustomerRiskViewPort riskViewPort;

    // ==================== EVENTOS DE CLIENTE ====================

    @KafkaListener(topics = Topics.CUSTOMER_REGISTERED, containerFactory = "customerRegisteredKafkaListenerContainerFactory")
    public void onCustomerRegistered(CustomerRegisteredEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: customer.registered.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] CustomerRegisteredEvent campos recibidos -> customerId={}, username={}, email={}, customerType={}, documentType={}, documentNumber=[MASKED], occurredAt={}",
                    event.getCustomerId(), event.getUsername(), event.getEmail(), event.getCustomerType(), event.getDocumentType(), "[MASKED]", event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: customer.registered.v1 - customerId={}", event.getCustomerId());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.CUSTOMER_REGISTERED, event.getCustomerId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Cliente registrado: {} - Creando customer_profile_view", event.getCustomerId());
                return createProfileView(event);
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento customer.registered: customerId={} - {}",
                    event.getCustomerId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = Topics.CUSTOMER_UPDATED, containerFactory = "customerUpdatedKafkaListenerContainerFactory")
    public void onCustomerUpdated(CustomerUpdatedEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: customer.updated.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] CustomerUpdatedEvent campos recibidos -> customerId={}, customerType={}, profile={}, status={}, occurredAt={}",
                    event.getCustomerId(), event.getCustomerType(), event.getProfile(), event.getStatus(), event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: customer.updated.v1 - customerId={}", event.getCustomerId());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.CUSTOMER_UPDATED, event.getCustomerId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Cliente actualizado: {} - Actualizando customer_profile_view", event.getCustomerId());
                return updateProfileView(event);
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento customer.updated: customerId={} - {}",
                    event.getCustomerId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = Topics.CUSTOMER_DELETED, containerFactory = "customerDeletedKafkaListenerContainerFactory")
    public void onCustomerDeleted(CustomerDeletedEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: customer.deleted.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] CustomerDeletedEvent campos recibidos -> customerId={}, occurredAt={}",
                    event.getCustomerId(), event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: customer.deleted.v1 - customerId={}", event.getCustomerId());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.CUSTOMER_DELETED, event.getCustomerId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Cliente eliminado: {} - Marcando vistas como DELETED", event.getCustomerId());
                return markViewsDeleted(event.getCustomerId());
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento customer.deleted: customerId={} - {}",
                    event.getCustomerId(), e.getMessage(), e);
        }
    }

    // ==================== EVENTOS DE TARJETA ====================

    @KafkaListener(topics = Topics.CARD_OPENED, containerFactory = "cardOpenedKafkaListenerContainerFactory")
    public void onCardOpened(CardOpenedEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: card.opened.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] CardOpenedEvent campos recibidos -> cardId={}, customerId={}, cardType={}, accountId={}, creditLimit={}, occurredAt={}",
                    event.getCardId(), event.getCustomerId(), event.getCardType(), event.getAccountId(), event.getCreditLimit(), event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: card.opened.v1 - cardId={}, customerId={}",
                event.getCardId(), event.getCustomerId());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.CARD_OPENED, event.getCardId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Tarjeta abierta: {} para cliente {} - Actualizando customer_card_view",
                        event.getCardId(), event.getCustomerId());
                return updateCardViewOpened(event);
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento card.opened: cardId={} - {}",
                    event.getCardId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = Topics.CARD_CLOSED, containerFactory = "cardClosedKafkaListenerContainerFactory")
    public void onCardClosed(CardClosedEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: card.closed.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] CardClosedEvent campos recibidos -> cardId={}, customerId={}, occurredAt={}",
                    event.getCardId(), event.getCustomerId(), event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: card.closed.v1 - cardId={}", event.getCardId());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.CARD_CLOSED, event.getCardId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Tarjeta cerrada: {} - Actualizando customer_card_view", event.getCardId());
                return updateCardViewClosed(event);
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento card.closed: cardId={} - {}",
                    event.getCardId(), e.getMessage(), e);
        }
    }

    // ==================== EVENTOS DE CRÉDITO (RIESGO) ====================

    @KafkaListener(topics = Topics.CREDIT_OVERDUE, containerFactory = "creditOverdueKafkaListenerContainerFactory")
    public void onCreditOverdue(CreditOverdueEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: credit.overdue.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] CreditOverdueEvent campos recibidos -> customerId={}, creditId={}, overdueAmount={}, daysOverdue={}, occurredAt={}",
                    event.getCustomerId(), event.getCreditId(), event.getOverdueAmount(), event.getDaysOverdue(), event.getOccurredAt());
        }
        log.warn("[KAFKA] Evento recibido: credit.overdue.v1 - customerId={}, overdueAmount={}",
                event.getCustomerId(), event.getOverdueAmount());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.CREDIT_OVERDUE, event.getCustomerId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.warn("[KAFKA] Deuda vencida detectada para cliente: {} - Actualizando customer_risk_view",
                        event.getCustomerId());
                return updateRiskView(event);
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento credit.overdue: customerId={} - {}",
                    event.getCustomerId(), e.getMessage(), e);
        }
    }

    // ==================== EVENTOS DE OPERACIONES ====================

    @KafkaListener(topics = Topics.DEBIT_PAYMENT_REQUESTED, containerFactory = "debitPaymentRequestedKafkaListenerContainerFactory")
    public void onDebitPaymentRequested(DebitPaymentRequestedEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: debit.payment.requested.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] DebitPaymentRequestedEvent campos recibidos -> paymentId={}, cardId={}, customerId={}, accountId={}, amount={}, occurredAt={}",
                    event.getPaymentId(), event.getCardId(), event.getCustomerId(), event.getAccountId(), event.getAmount(), event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: debit.payment.requested.v1 - paymentId={}, accountId={}, amount={}",
                event.getPaymentId(), event.getAccountId(), event.getAmount());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.DEBIT_PAYMENT_REQUESTED, event.getPaymentId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Pago débito solicitado: {} - Liquidando", event.getPaymentId());
                return accountService.settleDebitPayment(
                        new com.yanki.accounts.domain.service.DebitPaymentCommand(
                                event.getPaymentId(), event.getCardId(), event.getCustomerId(),
                                event.getAccountId(), event.getAmount(), event.getOccurredAt()));
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento debit.payment.requested: paymentId={} - {}",
                    event.getPaymentId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = Topics.TRANSFER_REQUESTED, containerFactory = "transferRequestedKafkaListenerContainerFactory")
    public void onTransferRequested(TransferRequestedEvent event) {
        log.info("[KAFKA-CONSUMER] Evento recibido: transfer.requested.v1 - clase: {}", event != null ? event.getClass().getName() : "null");
        if (event != null) {
            log.debug("[KAFKA-CONSUMER] TransferRequestedEvent campos recibidos -> transferId={}, sourceAccountId={}, targetAccountId={}, amount={}, description={}, requestedBy={}, occurredAt={}",
                    event.getTransferId(), event.getSourceAccountId(), event.getTargetAccountId(), event.getAmount(), event.getDescription(), event.getRequestedBy(), event.getOccurredAt());
        }
        log.info("[KAFKA] Evento recibido: transfer.requested.v1 - transferId={}, source={}, target={}, amount={}",
                event.getTransferId(), event.getSourceAccountId(), event.getTargetAccountId(), event.getAmount());
        try {
            String occurredAt = event.getOccurredAt() != null ? event.getOccurredAt().toString() : "no-date";
            String idempotencyKey = buildKey(Topics.TRANSFER_REQUESTED, event.getTransferId(), occurredAt);
            log.debug("[KAFKA] Clave de idempotencia: {}", idempotencyKey);
            processIfNew(idempotencyKey, () -> {
                log.info("[KAFKA] Transferencia solicitada: {} - Liquidando", event.getTransferId());
                return accountService.settleTransfer(
                        new com.yanki.accounts.domain.service.TransferCommand(
                                event.getTransferId(), event.getSourceAccountId(), event.getTargetAccountId(),
                                event.getAmount(), event.getDescription(), event.getRequestedBy(), event.getOccurredAt()));
            });
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento transfer.requested: transferId={} - {}",
                    event.getTransferId(), e.getMessage(), e);
        }
    }

    // ==================== HELPERS ====================

    private <T> Completable toCompletable(Single<T> single) {
        return single.ignoreElement();
    }

    private void processIfNew(String key, java.util.function.Supplier<Completable> action) {
        try {
            boolean processed = idempotencyPort.checkAndMarkProcessed(key)
                    .toFuture()
                    .get(10, java.util.concurrent.TimeUnit.SECONDS);

            if (processed) {
                log.debug("[KAFKA] Evento duplicado detectado, descartando: {}", key);
            } else {
                action.get().toFuture().get(30, java.util.concurrent.TimeUnit.SECONDS);
                log.info("[KAFKA] Evento procesado exitosamente: {}", key);
            }
        } catch (java.util.concurrent.TimeoutException e) {
            log.error("[KAFKA] Timeout procesando evento: {}", key, e);
            throw new RuntimeException("Timeout procesando evento Kafka: " + key, e);
        } catch (java.util.concurrent.ExecutionException e) {
            log.error("[KAFKA] Error procesando evento: {} - {}", key, e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
            throw new RuntimeException("Error procesando evento Kafka: " + key, e.getCause());
        } catch (Exception e) {
            log.error("[KAFKA] Error procesando evento: {} - {}", key, e.getMessage(), e);
            throw new RuntimeException("Error procesando evento Kafka: " + key, e);
        }
    }

    private String buildKey(String topic, String entityId, String occurredAt) {
        return topic + ":" + entityId + ":" + occurredAt;
    }

    // ==================== ACTUALIZACIÓN DE VISTAS LOCALES ====================

    private Completable createProfileView(CustomerRegisteredEvent event) {
        log.debug("[KAFKA] Creando customer_profile_view: customerId={}, type={}, roles={}",
                event.getCustomerId(), event.getCustomerType(), event.getRoles());
        try {
            CustomerProfileView view = CustomerProfileView.builder()
                    .customerId(event.getCustomerId())
                    .customerType(event.getCustomerType() != null ? event.getCustomerType() : "PERSONAL")
                    .profile("STANDARD")
                    .status("ACTIVE")
                    .documentType(event.getDocumentType() != null ? event.getDocumentType() : "DNI")
                    .documentNumber(event.getDocumentNumber() != null ? event.getDocumentNumber() : "")
                    .email(event.getEmail())
                    .updatedAt(event.getOccurredAt() != null ? event.getOccurredAt() : java.time.LocalDateTime.now())
                    .build();
            return toCompletable(profileViewPort.save(view))
                    .doOnComplete(() -> log.info("[KAFKA] customer_profile_view creada: customerId={}", event.getCustomerId()))
                    .doOnError(throwable -> log.error("[KAFKA] Error creando customer_profile_view: customerId={} - {}",
                            event.getCustomerId(), throwable.getMessage()));
        } catch (Exception e) {
            log.error("[KAFKA] Error inesperado creando customer_profile_view: customerId={} - {}",
                    event.getCustomerId(), e.getMessage(), e);
            return Completable.error(e);
        }
    }

    private Completable updateProfileView(CustomerUpdatedEvent event) {
        log.debug("[KAFKA] Actualizando customer_profile_view: customerId={}, type={}, profile={}, status={}",
                event.getCustomerId(), event.getCustomerType(), event.getProfile(), event.getStatus());
        return profileViewPort.findByCustomerId(event.getCustomerId())
                .toSingle()
                .map(view -> {
                    view.setCustomerType(event.getCustomerType());
                    view.setProfile(event.getProfile());
                    view.setStatus(event.getStatus());
                    view.setUpdatedAt(event.getOccurredAt());
                    return view;
                })
                .flatMap(profileViewPort::save)
                .doOnSuccess(v -> log.info("[KAFKA] customer_profile_view actualizada: customerId={}", event.getCustomerId()))
                .doOnError(throwable -> log.error("[KAFKA] Error actualizando customer_profile_view: customerId={} - {}",
                        event.getCustomerId(), throwable.getMessage()))
                .ignoreElement();
    }

    private Completable markViewsDeleted(String customerId) {
        log.debug("[KAFKA] Marcando vistas como DELETED: customerId={}", customerId);
        Completable profileDelete = profileViewPort.findByCustomerId(customerId)
                .toSingle()
                .flatMapCompletable(view -> {
                    view.setStatus("DELETED");
                    view.setUpdatedAt(java.time.LocalDateTime.now());
                    return profileViewPort.save(view).ignoreElement()
                            .doOnComplete(() -> log.debug("[KAFKA] customer_profile_view marcada como DELETED: customerId={}", customerId));
                })
                .onErrorResumeNext(throwable -> {
                    log.warn("[KAFKA] customer_profile_view no encontrada para marcar DELETED: customerId={}", customerId);
                    return Completable.complete();
                });

        Completable cardDelete = cardViewPort.findByCustomerId(customerId)
                .toSingle()
                .flatMapCompletable(view -> {
                    view.setHasCreditCard(false);
                    view.setUpdatedAt(java.time.LocalDateTime.now());
                    return cardViewPort.save(view).ignoreElement()
                            .doOnComplete(() -> log.debug("[KAFKA] customer_card_view marcada como DELETED: customerId={}", customerId));
                })
                .onErrorResumeNext(throwable -> {
                    log.warn("[KAFKA] customer_card_view no encontrada para marcar DELETED: customerId={}", customerId);
                    return Completable.complete();
                });

        Completable riskDelete = riskViewPort.findByCustomerId(customerId)
                .toSingle()
                .flatMapCompletable(view -> {
                    view.setHasOverdueDebt(false);
                    view.setUpdatedAt(java.time.LocalDateTime.now());
                    return riskViewPort.save(view).ignoreElement()
                            .doOnComplete(() -> log.debug("[KAFKA] customer_risk_view marcada como DELETED: customerId={}", customerId));
                })
                .onErrorResumeNext(throwable -> {
                    log.warn("[KAFKA] customer_risk_view no encontrada para marcar DELETED: customerId={}", customerId);
                    return Completable.complete();
                });

        return profileDelete.andThen(cardDelete).andThen(riskDelete)
                .doOnComplete(() -> log.info("[KAFKA] Todas las vistas marcadas como DELETED: customerId={}", customerId))
                .doOnError(throwable -> log.error("[KAFKA] Error marcando vistas como DELETED: customerId={} - {}",
                        customerId, throwable.getMessage()));
    }

    private Completable updateCardViewOpened(CardOpenedEvent event) {
        log.debug("[KAFKA] Actualizando customer_card_view (abierta): cardId={}, customerId={}, cardType={}",
                event.getCardId(), event.getCustomerId(), event.getCardType());
        return cardViewPort.findByCustomerId(event.getCustomerId())
                .toSingle()
                .map(view -> {
                    if ("CREDIT".equals(event.getCardType())) {
                        view.setHasCreditCard(true);
                        view.setCreditCardId(event.getCardId());
                        view.setUpdatedAt(event.getOccurredAt());
                        log.debug("[KAFKA] Tarjeta de crédito registrada: cardId={}, customerId={}",
                                event.getCardId(), event.getCustomerId());
                    } else {
                        log.debug("[KAFKA] Tarjeta no es de crédito, ignorando: cardId={}, cardType={}",
                                event.getCardId(), event.getCardType());
                    }
                    return view;
                })
                .flatMap(cardViewPort::save)
                .doOnSuccess(v -> log.info("[KAFKA] customer_card_view actualizada (abierta): customerId={}", event.getCustomerId()))
                .doOnError(throwable -> log.error("[KAFKA] Error actualizando customer_card_view (abierta): customerId={} - {}",
                        event.getCustomerId(), throwable.getMessage()))
                .ignoreElement();
    }

    private Completable updateCardViewClosed(CardClosedEvent event) {
        log.debug("[KAFKA] Actualizando customer_card_view (cerrada): cardId={}, customerId={}",
                event.getCardId(), event.getCustomerId());
        return cardViewPort.findByCustomerId(event.getCustomerId())
                .toSingle()
                .map(view -> {
                    view.setHasCreditCard(false);
                    view.setCreditCardId(null);
                    view.setUpdatedAt(event.getOccurredAt());
                    return view;
                })
                .flatMap(cardViewPort::save)
                .doOnSuccess(v -> log.info("[KAFKA] customer_card_view actualizada (cerrada): customerId={}", event.getCustomerId()))
                .doOnError(throwable -> log.error("[KAFKA] Error actualizando customer_card_view (cerrada): customerId={} - {}",
                        event.getCustomerId(), throwable.getMessage()))
                .ignoreElement();
    }

    private Completable updateRiskView(CreditOverdueEvent event) {
        log.warn("[KAFKA] Actualizando customer_risk_view: customerId={}, overdueAmount={}, daysOverdue={}",
                event.getCustomerId(), event.getOverdueAmount(), event.getDaysOverdue());
        return riskViewPort.findByCustomerId(event.getCustomerId())
                .toSingle()
                .map(view -> {
                    view.setHasOverdueDebt(true);
                    view.setOverdueAmount(event.getOverdueAmount());
                    view.setCreditId(event.getCreditId());
                    view.setDaysOverdue(event.getDaysOverdue() != null ? event.getDaysOverdue() : 0);
                    view.setUpdatedAt(event.getOccurredAt());
                    return view;
                })
                .flatMap(riskViewPort::save)
                .doOnSuccess(v -> log.warn("[KAFKA] customer_risk_view actualizada (deuda vencida): customerId={}", event.getCustomerId()))
                .doOnError(throwable -> log.error("[KAFKA] Error actualizando customer_risk_view: customerId={} - {}",
                        event.getCustomerId(), throwable.getMessage()))
                .ignoreElement();
    }
}