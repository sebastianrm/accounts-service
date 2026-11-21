package com.yanki.accounts.infrastructure.persistence;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Evento procesado para garantizar idempotencia en el consumo de Kafka.
 *
 * <p>Clave única: {@code <topic>:<entityId>:<occurredAt>} según contrato EVENTOS.md.
 * Cada evento consumido se registra aquí para evitar procesamiento duplicado.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "processed_events")
public class ProcessedEvent {

    /** Clave de idempotencia: topic:entityId:occurredAt */
    @Id
    private String id;

    /** Topic del evento. */
    private String topic;

    /** Identificador de la entidad (accountId, customerId, etc.). */
    private String entityId;

    /** Timestamp del evento (occurredAt). */
    private String occurredAt;

    /** Fecha en que se procesó el evento. */
    private LocalDateTime processedAt;
}