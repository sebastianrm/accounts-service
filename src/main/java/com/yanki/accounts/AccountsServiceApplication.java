package com.yanki.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Punto de entrada del microservicio accounts-service.
 *
 * <p>Este servicio sigue el patrón database-per-service, gestionando su propia
 * base de datos MongoDB e interactuando con otros servicios a través de eventos Kafka.
 * Garantiza que no se realizan llamadas REST directas para procesos entre servicios
 * dentro de la plataforma Yanki.</p>
 *
 * <p>Componentes principales:</p>
 * <ul>
 *   <li>Gestión de cuentas pasivas: ahorro, corriente, plazo fijo</li>
 *   <li>Proyecciones locales alimentadas por eventos Kafka (vistas de cliente, tarjetas, riesgo)</li>
 *   <li>Publicación de eventos de dominio hacia Kafka</li>
 *   <li>Caché distribuida con Redis para saldos de cuentas</li>
 * </ul>
 *
 * @see <a href="https://github.com/yanki/accounts-service">Repository</a>
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@SpringBootApplication
@EnableScheduling
public class AccountsServiceApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments forwarded to the Spring context
     */
    public static void main(String[] args) {
        SpringApplication.run(AccountsServiceApplication.class, args);
    }
}
