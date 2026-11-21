package com.yanki.accounts.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferCommandTest {

    @Test
    void builder_setsAllFields() {
        TransferCommand command = TransferCommand.builder()
                .sourceAccountId("ACC001")
                .targetAccountId("ACC002")
                .amount(new BigDecimal("300.00"))
                .transferId("TRF001")
                .description("Transferencia")
                .build();

        assertThat(command.getSourceAccountId()).isEqualTo("ACC001");
        assertThat(command.getTargetAccountId()).isEqualTo("ACC002");
        assertThat(command.getAmount()).isEqualTo(new BigDecimal("300.00"));
        assertThat(command.getTransferId()).isEqualTo("TRF001");
    }
}
