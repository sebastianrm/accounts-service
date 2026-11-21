package com.yanki.accounts.domain.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de una operación de consulta de cuenta bancaria.
 *
 * <p>DTO inmutable que agrupa toda la información relevante de una cuenta
 * para ser retornada por los servicios de dominio. Incluye datos básicos,
 * saldo, configuración de comisiones y, en el caso de cuentas empresariales,
 * la lista de titulares y firmantes autorizados.</p>
 *
 * @author Sebastián Retamal
 * @contact semorale@emeal.nttdata.com
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResult {

    /** Identificador de la cuenta. */
    private String accountId;

    /** Número de cuenta (generado). */
    private String accountNumber;

    /** Tipo de cuenta. */
    private String accountType;

    /** Estado. */
    private String status;

    /** Saldo actual. */
    private BigDecimal balance;

    /** Moneda. */
    private String currency;

    /** Fecha de apertura. */
    private LocalDateTime openedAt;

    /** Identificador del cliente. */
    private String customerId;

    /** Comisión de mantenimiento mensual. */
    private BigDecimal maintenanceCommission;

    /** Titulares (solo corriente empresarial). */
    private List<AccountHolderInfo> holders;

    /** Firmantes (solo corriente empresarial). */
    private List<AccountSignerInfo> signers;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountHolderInfo {
        private String holderId;
        private Integer percentage;
        private Boolean principal;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountSignerInfo {
        private String signerId;
        private BigDecimal operationLimit;
        private Boolean active;
    }
}