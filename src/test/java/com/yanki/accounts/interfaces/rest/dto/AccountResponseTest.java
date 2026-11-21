package com.yanki.accounts.interfaces.rest.dto;

import com.yanki.accounts.domain.service.AccountResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountResponseTest {

    @Test
    void from_convertsAccountResultToResponse() {
        AccountResult result = AccountResult.builder()
                .accountId("ACC001")
                .accountNumber("ACC1234567890123456")
                .accountType("SAVINGS")
                .status("OPEN")
                .balance(new BigDecimal("1000.00"))
                .currency("PEN")
                .openedAt(LocalDateTime.of(2026, 1, 1, 10, 0))
                .customerId("CUST001")
                .maintenanceCommission(BigDecimal.ZERO)
                .build();

        AccountResponse response = AccountResponse.from(result);

        assertThat(response.getAccountId()).isEqualTo("ACC001");
        assertThat(response.getAccountNumber()).isEqualTo("ACC1234567890123456");
        assertThat(response.getAccountType()).isEqualTo("SAVINGS");
        assertThat(response.getStatus()).isEqualTo("OPEN");
        assertThat(response.getBalance()).isEqualTo(new BigDecimal("1000.00"));
    }
}
