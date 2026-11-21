package com.yanki.accounts.infrastructure.persistence.mapper;

import com.yanki.accounts.domain.model.Account;
import com.yanki.accounts.domain.model.AccountHolder;
import com.yanki.accounts.domain.model.AccountSigner;
import com.yanki.accounts.infrastructure.persistence.document.AccountDocument;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Traductor estático entre {@link Account} (dominio) y {@link AccountDocument} (persistencia MongoDB).
 *
 * <p>Convierte enums a String y viceversa para el mapeo de tipo y estado.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
public final class AccountDocumentMapper {

    private AccountDocumentMapper() {
        // Utility class
    }

    /**
     * Convierte una entidad de dominio a documento de persistencia.
     *
     * @param account entidad de dominio
     * @return documento MongoDB
     */
    public static AccountDocument toDocument(Account account) {
        if (account == null) {
            return null;
        }

        AccountDocument.AccountDocumentBuilder builder = AccountDocument.builder()
                .id(account.getId())
                .customerId(account.getCustomerId())
                .accountNumber(account.getAccountNumber())
                .type(account.getType() != null ? account.getType().name() : null)
                .status(account.getStatus() != null ? account.getStatus().name() : null)
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .minOpeningAmount(account.getMinOpeningAmount())
                .maxFreeMovementsPerMonth(account.getMaxFreeMovementsPerMonth())
                .commissionPerExcessMovement(account.getCommissionPerExcessMovement())
                .maintenanceCommission(account.getMaintenanceCommission())
                .maturityDate(account.getMaturityDate())
                .allowedMovementDay(account.getAllowedMovementDay())
                .openedAt(account.getOpenedAt())
                .closedAt(account.getClosedAt())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .monthlyMovementCount(account.getMonthlyMovementCount())
                .currentMonth(account.getCurrentMonth());

        if (account.getHolders() != null) {
            builder.holders(account.getHolders().stream()
                    .map(h -> AccountDocument.AccountHolderDocument.builder()
                            .holderId(h.getHolderId())
                            .percentage(h.getPercentage() != null ? new java.math.BigDecimal(h.getPercentage()) : null)
                            .principal(h.getPrincipal())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (account.getSigners() != null) {
            builder.signers(account.getSigners().stream()
                    .map(s -> AccountDocument.AccountSignerDocument.builder()
                            .signerId(s.getSignerId())
                            .operationLimit(s.getOperationLimit())
                            .active(s.getActive())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    /**
     * Convierte un documento de persistencia a entidad de dominio.
     *
     * @param document documento MongoDB
     * @return entidad de dominio
     */
    public static Account toDomain(AccountDocument document) {
        if (document == null) {
            return null;
        }

        Account.AccountBuilder builder = Account.builder()
                .id(document.getId())
                .customerId(document.getCustomerId())
                .accountNumber(document.getAccountNumber())
                .type(com.yanki.accounts.domain.model.AccountType.valueOf(document.getType()))
                .status(com.yanki.accounts.domain.model.AccountStatus.valueOf(document.getStatus()))
                .currency(document.getCurrency())
                .balance(document.getBalance())
                .minOpeningAmount(document.getMinOpeningAmount())
                .maxFreeMovementsPerMonth(document.getMaxFreeMovementsPerMonth())
                .commissionPerExcessMovement(document.getCommissionPerExcessMovement())
                .maintenanceCommission(document.getMaintenanceCommission())
                .maturityDate(document.getMaturityDate())
                .allowedMovementDay(document.getAllowedMovementDay())
                .openedAt(document.getOpenedAt())
                .closedAt(document.getClosedAt())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .monthlyMovementCount(document.getMonthlyMovementCount())
                .currentMonth(document.getCurrentMonth());

        if (document.getHolders() != null) {
            builder.holders(document.getHolders().stream()
                    .map(h -> AccountHolder.builder()
                            .holderId(h.getHolderId())
                            .percentage(h.getPercentage() != null ? h.getPercentage().intValue() : null)
                            .principal(h.getPrincipal())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (document.getSigners() != null) {
            builder.signers(document.getSigners().stream()
                    .map(s -> AccountSigner.builder()
                            .signerId(s.getSignerId())
                            .operationLimit(s.getOperationLimit())
                            .active(s.getActive())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }

    /**
     * Convierte una lista de documentos a lista de entidades de dominio.
     *
     * @param documents lista de documentos MongoDB
     * @return lista de entidades de dominio
     */
    public static List<Account> toDomainList(List<AccountDocument> documents) {
        if (documents == null) {
            return List.of();
        }
        return documents.stream().map(AccountDocumentMapper::toDomain).collect(Collectors.toList());
    }
}
