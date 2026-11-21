package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CardClosedEventTest {

    @Test
    void builder_setsAllFields() {
        CardClosedEvent event = CardClosedEvent.builder()
                .cardId("CC001")
                .customerId("CUST001")
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(event.getCardId()).isEqualTo("CC001");
        assertThat(event.getCustomerId()).isEqualTo("CUST001");
    }
}
