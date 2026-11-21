package com.yanki.accounts.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración de la caché Redis para accounts-service.
 *
 * <p>Define el TTL para saldos de cuentas y el prefijo de claves
 * utilizado en Redis para evitar colisiones entre servicios.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "yanki.accounts.cache")
public class CacheProperties {

    /** TTL para el saldo de cuentas. */
    private Duration balanceTtl = Duration.ofMinutes(5);

    /** Prefijo de claves para este servicio. */
    private String keyPrefix = "yanki:accounts:";

    /** TTL para contadores de movimientos mensuales (hasta fin de mes + margen). */
    private Duration movementCountTtl = Duration.ofDays(32);
}