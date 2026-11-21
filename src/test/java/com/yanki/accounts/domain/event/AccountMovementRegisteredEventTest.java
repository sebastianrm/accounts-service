package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountMovementRegisteredEventTest {

    @Test
    void builder_setsAllFields() {
        AccountMovementRegisteredEvent event = AccountMovementRegisteredEvent.builder()
                .movementId("MOV001")
                .accountId("ACC001")
                .customerId("CUST001")
                .movementType("DEPOSIT")
                .amount(new BigDecimal("500.00"))
                .commission(BigDecimal.ZERO)
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(event.getMovementId()).isEqualTo("MOV001");
        assertThat(event.getAccountId()).isEqualTo("ACC001");
    }
}
