package com.yanki.accounts.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAccountCommandTest {

    @Test
    void builder_setsAllFields() {
        OpenAccountCommand command = OpenAccountCommand.builder()
                .customerId("CUST001")
                .accountType("SAVINGS")
                .initialAmount(new BigDecimal("100.00"))
                .channel("WEB")
                .referenceId("REF001")
                .build();

        assertThat(command.getCustomerId()).isEqualTo("CUST001");
        assertThat(command.getAccountType()).isEqualTo("SAVINGS");
        assertThat(command.getInitialAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(command.getChannel()).isEqualTo("WEB");
        assertThat(command.getReferenceId()).isEqualTo("REF001");
    }
}
