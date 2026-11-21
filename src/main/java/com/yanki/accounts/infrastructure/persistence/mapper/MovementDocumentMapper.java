package com.yanki.accounts.infrastructure.persistence.mapper;

import com.yanki.accounts.domain.model.Movement;
import com.yanki.accounts.infrastructure.persistence.document.MovementDocument;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Traductor estático entre {@link Movement} (dominio) y {@link MovementDocument} (persistencia MongoDB).
 *
 * <p>Convierte enums a String y viceversa para el mapeo de tipo de movimiento.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public final class MovementDocumentMapper {

    private MovementDocumentMapper() {
        // Utility class
    }

    /**
     * Convierte una entidad de dominio a documento de persistencia.
     *
     * @param movement entidad de dominio
     * @return documento MongoDB
     */
    public static MovementDocument toDocument(Movement movement) {
        if (movement == null) {
            return null;
        }

        return MovementDocument.builder()
                .id(movement.getId())
                .accountId(movement.getAccountId())
                .customerId(movement.getCustomerId())
                .type(movement.getType() != null ? movement.getType().name() : null)
                .amount(movement.getAmount())
                .balanceAfter(movement.getBalanceAfter())
                .commission(movement.getCommission())
                .channel(movement.getChannel())
                .description(movement.getDescription())
                .referenceId(movement.getReferenceId())
                .occurredAt(movement.getOccurredAt())
                .createdAt(movement.getCreatedAt())
                .build();
    }

    /**
     * Convierte un documento de persistencia a entidad de dominio.
     *
     * @param document documento MongoDB
     * @return entidad de dominio
     */
    public static Movement toDomain(MovementDocument document) {
        if (document == null) {
            return null;
        }

        return Movement.builder()
                .id(document.getId())
                .accountId(document.getAccountId())
                .customerId(document.getCustomerId())
                .type(com.yanki.accounts.domain.model.MovementType.valueOf(document.getType()))
                .amount(document.getAmount())
                .balanceAfter(document.getBalanceAfter())
                .commission(document.getCommission())
                .channel(document.getChannel())
                .description(document.getDescription())
                .referenceId(document.getReferenceId())
                .occurredAt(document.getOccurredAt())
                .createdAt(document.getCreatedAt())
                .build();
    }

    /**
     * Convierte una lista de documentos a lista de entidades de dominio.
     *
     * @param documents lista de documentos MongoDB
     * @return lista de entidades de dominio
     */
    public static List<Movement> toDomainList(List<MovementDocument> documents) {
        if (documents == null) {
            return List.of();
        }
        return documents.stream().map(MovementDocumentMapper::toDomain).collect(Collectors.toList());
    }
}
