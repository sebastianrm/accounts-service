package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountOpenedEventTest {

    @Test
    void builder_setsAllFields() {
        AccountOpenedEvent event = AccountOpenedEvent.builder()
                .accountId("ACC001")
                .customerId("CUST001")
                .productType("SAVINGS")
                .balance(new BigDecimal("100.00"))
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(event.getAccountId()).isEqualTo("ACC001");
        assertThat(event.getCustomerId()).isEqualTo("CUST001");
        assertThat(event.getProductType()).isEqualTo("SAVINGS");
    }
}
