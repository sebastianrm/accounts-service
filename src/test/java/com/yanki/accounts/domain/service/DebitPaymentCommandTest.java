package com.yanki.accounts.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DebitPaymentCommandTest {

    @Test
    void builder_setsAllFields() {
        DebitPaymentCommand command = DebitPaymentCommand.builder()
                .accountId("ACC001")
                .amount(new BigDecimal("100.00"))
                .paymentId("PAY001")
                .customerId("CUST001")
                .build();

        assertThat(command.getAccountId()).isEqualTo("ACC001");
        assertThat(command.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(command.getPaymentId()).isEqualTo("PAY001");
        assertThat(command.getCustomerId()).isEqualTo("CUST001");
    }
}
