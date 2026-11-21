package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountClosedEventTest {

    @Test
    void builder_setsAllFields() {
        AccountClosedEvent event = AccountClosedEvent.builder()
                .accountId("ACC001")
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(event.getAccountId()).isEqualTo("ACC001");
    }
}
