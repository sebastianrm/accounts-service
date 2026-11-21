package com.yanki.accounts.domain.service;

import com.yanki.accounts.domain.port.out.AccountPersistencePort;
import com.yanki.accounts.domain.port.out.CachePort;
import com.yanki.accounts.domain.port.out.EventPublisherPort;
import com.yanki.accounts.domain.port.out.MovementPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * Tests para AccountSchedulerService — Jobs programados de comisiones.
 *
 * <p>Cubre los casos de prueba de CU-15 (Comisiones):</p>
 * <ul>
 *   <li>CP-142: Job limpieza contadores</li>
 * </ul>
 *
 * <p>NOTA: Los tests de CP-139, CP-140, CP-141 y CP-144 (comisión de mantenimiento)
 * no se pueden ejecutar porque AccountSchedulerService.applyMaintenanceCommission()
 * contiene un error de compilación: usa {@code Single.toCompletable()} que no existe
 * en RxJava 3. Se requiere corregir el código de producción antes de poder probar
 * estos casos.</p>
 */
@ExtendWith(MockitoExtension.class)
class AccountSchedulerServiceTest {

    @Mock
    private AccountPersistencePort accountPersistencePort;

    @Mock
    private MovementPersistencePort movementPersistencePort;

    @Mock
    private EventPublisherPort eventPublisherPort;

    @Mock
    private CachePort cachePort;

    private AccountSchedulerService accountSchedulerService;

    @BeforeEach
    void setUp() {
        accountSchedulerService = new AccountSchedulerService(
                accountPersistencePort,
                movementPersistencePort,
                eventPublisherPort,
                cachePort
        );
    }

    // ==================== Cleanup Movement Counters Job (CU-15, CP-142) ====================

    @Nested
    @DisplayName("Job de limpieza de contadores")
    class CleanupMovementCountersJobTests {

        @Test
        @DisplayName("Debe limpiar contadores de movimientos del mes anterior (CP-142)")
        void cleanupMovementCounters_success() {
            doNothing().when(cachePort).cleanupMovementCounters("2026-10");

            accountSchedulerService.triggerCleanupMovementCounters();

            verify(cachePort).cleanupMovementCounters("2026-10");
        }
    }
}
