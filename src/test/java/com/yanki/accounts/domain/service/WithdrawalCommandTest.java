package com.yanki.accounts.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class WithdrawalCommandTest {

    @Test
    void builder_setsAllFields() {
        WithdrawalCommand command = WithdrawalCommand.builder()
                .accountId("ACC001")
                .amount(new BigDecimal("200.00"))
                .description("Retiro")
                .channel("ATM")
                .build();

        assertThat(command.getAccountId()).isEqualTo("ACC001");
        assertThat(command.getAmount()).isEqualTo(new BigDecimal("200.00"));
        assertThat(command.getDescription()).isEqualTo("Retiro");
        assertThat(command.getChannel()).isEqualTo("ATM");
    }
}
