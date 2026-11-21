package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovementTest {

    @Test
    void builder_setsAllFields() {
        Movement movement = Movement.builder()
                .id("MOV001")
                .accountId("ACC001")
                .customerId("CUST001")
                .type(MovementType.DEPOSIT)
                .amount(new java.math.BigDecimal("500.00"))
                .commission(java.math.BigDecimal.ZERO)
                .channel("WEB")
                .description("Depósito")
                .referenceId("REF001")
                .balanceAfter(new java.math.BigDecimal("1500.00"))
                .build();

        assertThat(movement.getId()).isEqualTo("MOV001");
        assertThat(movement.getAccountId()).isEqualTo("ACC001");
        assertThat(movement.getType()).isEqualTo(MovementType.DEPOSIT);
        assertThat(movement.getAmount()).isEqualTo(new java.math.BigDecimal("500.00"));
        assertThat(movement.getBalanceAfter()).isEqualTo(new java.math.BigDecimal("1500.00"));
    }

    @Test
    void movementType_enumValues() {
        assertThat(MovementType.DEPOSIT).isNotNull();
        assertThat(MovementType.WITHDRAWAL).isNotNull();
        assertThat(MovementType.COMMISSION).isNotNull();
        assertThat(MovementType.INTEREST).isNotNull();
        assertThat(MovementType.DEBIT_PAYMENT).isNotNull();
        assertThat(MovementType.TRANSFER_OUT).isNotNull();
        assertThat(MovementType.TRANSFER_IN).isNotNull();
        assertThat(MovementType.OPENING_DEPOSIT).isNotNull();
    }
}
