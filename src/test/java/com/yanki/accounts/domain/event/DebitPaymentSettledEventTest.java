package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DebitPaymentSettledEventTest {

    @Test
    void builder_setsAllFields() {
        DebitPaymentSettledEvent event = DebitPaymentSettledEvent.builder()
                .paymentId("PAY001")
                .cardId("CC001")
                .status("SETTLED")
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(event.getPaymentId()).isEqualTo("PAY001");
        assertThat(event.getCardId()).isEqualTo("CC001");
    }
}
