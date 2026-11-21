package com.yanki.accounts.infrastructure.cache.adapter;

import com.yanki.accounts.domain.port.out.CachePort;
import com.yanki.accounts.infrastructure.config.CacheProperties;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collection;

/**
 * Adaptador de salida que implementa la caché de saldos sobre Redis Reactivo.
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheAdapter implements CachePort {

    private static final String BALANCE_KEY_PREFIX = "balance:";
    private static final String MOVEMENT_COUNT_KEY_PREFIX = "movement-count:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final CacheProperties cacheProperties;

    @Override
    public Single<BigDecimal> getBalance(String accountId) {
        String cacheKey = cacheProperties.getKeyPrefix() + BALANCE_KEY_PREFIX + accountId;
        log.debug("[Redis] Obteniendo saldo en caché: accountId={}, key={}", accountId, cacheKey);
        return Single.fromPublisher(
                redisTemplate.opsForValue().get(cacheKey).map(BigDecimal::new)
        ).doOnSuccess(balance -> log.debug("[Redis] Saldo obtenido de caché: accountId={}, balance={}", accountId, balance))
         .onErrorReturn(e -> {
             log.error("[Redis] Error obteniendo saldo de caché: accountId={} - {}", accountId, e.getMessage());
             return BigDecimal.ZERO;
         });
    }

    @Override
    public void setBalance(String accountId, BigDecimal balance) {
        String cacheKey = cacheProperties.getKeyPrefix() + BALANCE_KEY_PREFIX + accountId;
        log.debug("[Redis] Guardando saldo en caché: accountId={}, balance={}, ttl={}",
                accountId, balance, cacheProperties.getBalanceTtl());
        try {
            redisTemplate.opsForValue()
                    .set(cacheKey, balance.toPlainString(), cacheProperties.getBalanceTtl())
                    .subscribe(
                            v -> log.debug("[Redis] Saldo guardado en caché: accountId={}", accountId),
                            error -> log.error("[Redis] Error guardando saldo en caché: accountId={} - {}", accountId, error.getMessage())
                    );
        } catch (Exception e) {
            log.error("[Redis] Error inesperado guardando saldo en caché: accountId={} - {}", accountId, e.getMessage(), e);
        }
    }

    @Override
    public void invalidateBalance(String accountId) {
        String cacheKey = cacheProperties.getKeyPrefix() + BALANCE_KEY_PREFIX + accountId;
        log.debug("[Redis] Invalidando saldo en caché: accountId={}", accountId);
        try {
            redisTemplate.delete(cacheKey).subscribe(
                    v -> log.debug("[Redis] Saldo invalidado en caché: accountId={}", accountId),
                    error -> log.error("[Redis] Error invalidando saldo en caché: accountId={} - {}", accountId, error.getMessage())
            );
        } catch (Exception e) {
            log.error("[Redis] Error inesperado invalidando saldo en caché: accountId={} - {}", accountId, e.getMessage(), e);
        }
    }

    @Override
    public Single<Integer> incrementMovementCount(String accountId, String month) {
        String cacheKey = cacheProperties.getKeyPrefix() + MOVEMENT_COUNT_KEY_PREFIX + accountId + ":" + month;
        log.debug("[Redis] Incrementando contador de movimientos: accountId={}, month={}", accountId, month);
        return Single.fromPublisher(
                redisTemplate.opsForValue().increment(cacheKey)
        ).map(Long::intValue)
         .doOnSuccess(count -> {
             log.debug("[Redis] Contador incrementado: accountId={}, month={}, count={}", accountId, month, count);
             if (count == 1) {
                 redisTemplate.expire(cacheKey, cacheProperties.getMovementCountTtl()).subscribe();
             }
         })
         .onErrorReturn(e -> {
             log.error("[Redis] Error incrementando contador: accountId={} - {}", accountId, e.getMessage());
             return 0;
         });
    }

    @Override
    public Single<Integer> getMovementCount(String accountId, String month) {
        String cacheKey = cacheProperties.getKeyPrefix() + MOVEMENT_COUNT_KEY_PREFIX + accountId + ":" + month;
        log.debug("[Redis] Obteniendo contador de movimientos: accountId={}, month={}", accountId, month);
        return Single.fromPublisher(
                redisTemplate.opsForValue().get(cacheKey)
        ).map(count -> {
            int result = count == null ? 0 : Integer.parseInt(count);
            log.debug("[Redis] Contador obtenido: accountId={}, month={}, count={}", accountId, month, result);
            return result;
        }).onErrorReturn(e -> {
            log.error("[Redis] Error obteniendo contador: accountId={} - {}", accountId, e.getMessage());
            return 0;
        });
    }

    @Override
    public void cleanupMovementCounters(String month) {
        String pattern = cacheProperties.getKeyPrefix() + MOVEMENT_COUNT_KEY_PREFIX + "*:" + month;
        log.info("[Redis] Limpiando contadores de movimientos: month={}, pattern={}", month, pattern);
        try {
            redisTemplate.keys(pattern)
                    .flatMap(redisTemplate::delete)
                    .subscribe(
                            v -> log.info("[Redis] Contadores de movimientos limpiados: month={}", month),
                            error -> log.error("[Redis] Error limpiando contadores: month={} - {}", month, error.getMessage())
                    );
        } catch (Exception e) {
            log.error("[Redis] Error inesperado limpiando contadores: month={} - {}", month, e.getMessage(), e);
        }
    }
}
