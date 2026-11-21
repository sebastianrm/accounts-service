package com.yanki.accounts.infrastructure.persistence.adapter;

import com.yanki.accounts.domain.port.out.IdempotencyPort;
import com.yanki.accounts.infrastructure.persistence.ProcessedEvent;
import com.yanki.accounts.infrastructure.persistence.ProcessedEventRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

/**
 * Adaptador de salida que implementa el control de idempotencia sobre MongoDB.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyAdapter implements IdempotencyPort {

    private final ProcessedEventRepository processedEventRepository;

    @Override
    public Single<Boolean> checkAndMarkProcessed(String idempotencyKey) {
        log.debug("[Idempotency] Verificando idempotencia: key={}", idempotencyKey);
        return Single.fromPublisher(
                processedEventRepository.findById(idempotencyKey)
                        .map(existing -> {
                            log.debug("[Idempotency] Evento ya procesado (duplicado): key={}", idempotencyKey);
                            return true;
                        })
                        .switchIfEmpty(
                                processedEventRepository.save(createProcessedEvent(idempotencyKey))
                                        .map(saved -> {
                                            log.debug("[Idempotency] Evento marcado como procesado: key={}", idempotencyKey);
                                            return false;
                                        })
                        )
        ).onErrorResumeNext(throwable -> {
            log.error("[Idempotency] Error verificando idempotencia: key={} - {}", idempotencyKey, throwable.getMessage());
            return Single.just(false);
        });
    }

    private ProcessedEvent createProcessedEvent(String key) {
        try {
            String[] parts = key.split(":", 3);
            return ProcessedEvent.builder()
                    .id(key)
                    .topic(parts[0])
                    .entityId(parts[1])
                    .occurredAt(parts.length > 2 ? parts[2] : "")
                    .processedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("[Idempotency] Error creando ProcessedEvent: key={} - {}", key, e.getMessage(), e);
            throw e;
        }
    }
}
