package com.yanki.accounts.interfaces.rest.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MovementResponseTest {

    @Test
    void builder_setsAllFields() {
        MovementResponse response = MovementResponse.builder()
                .movementId("MOV001")
                .accountId("ACC001")
                .movementType("DEPOSIT")
                .amount(new BigDecimal("500.00"))
                .balanceAfter(new BigDecimal("1500.00"))
                .commission(BigDecimal.ZERO)
                .description("Deposito")
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(response.getMovementId()).isEqualTo("MOV001");
        assertThat(response.getAccountId()).isEqualTo("ACC001");
        assertThat(response.getMovementType()).isEqualTo("DEPOSIT");
    }
}
