package com.yanki.accounts.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DepositCommandTest {

    @Test
    void builder_setsAllFields() {
        DepositCommand command = DepositCommand.builder()
                .accountId("ACC001")
                .amount(new BigDecimal("500.00"))
                .description("Deposito")
                .channel("WEB")
                .referenceId("REF001")
                .build();

        assertThat(command.getAccountId()).isEqualTo("ACC001");
        assertThat(command.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(command.getDescription()).isEqualTo("Deposito");
        assertThat(command.getChannel()).isEqualTo("WEB");
    }
}
