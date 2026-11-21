package com.yanki.accounts.interfaces.rest.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRequestTest {

    @Test
    void builder_setsAllFields() {
        AccountRequest request = AccountRequest.builder()
                .customerId("CUST001")
                .accountType("SAVINGS")
                .initialAmount(new BigDecimal("100.00"))
                .channel("WEB")
                .build();

        assertThat(request.getCustomerId()).isEqualTo("CUST001");
        assertThat(request.getAccountType()).isEqualTo("SAVINGS");
        assertThat(request.getInitialAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(request.getChannel()).isEqualTo("WEB");
    }

    @Test
    void setters_work() {
        AccountRequest request = new AccountRequest();
        request.setCustomerId("CUST001");
        request.setAccountType("CURRENT");
        request.setInitialAmount(new BigDecimal("0.00"));
        request.setChannel("MOBILE");

        assertThat(request.getCustomerId()).isEqualTo("CUST001");
        assertThat(request.getAccountType()).isEqualTo("CURRENT");
    }

    @Test
    void toCommand_createsCommand() {
        AccountRequest request = AccountRequest.builder()
                .customerId("CUST001")
                .accountType("SAVINGS")
                .initialAmount(new BigDecimal("100.00"))
                .channel("WEB")
                .build();

        var command = request.toCommand();
        assertThat(command.getCustomerId()).isEqualTo("CUST001");
        assertThat(command.getAccountType()).isEqualTo("SAVINGS");
    }
}
