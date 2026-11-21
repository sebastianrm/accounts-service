package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransferSettledEventTest {

    @Test
    void builder_setsAllFields() {
        TransferSettledEvent event = TransferSettledEvent.builder()
                .transferId("TRF001")
                .sourceAccountId("ACC001")
                .occurredAt(LocalDateTime.now())
                .build();

        assertThat(event.getTransferId()).isEqualTo("TRF001");
        assertThat(event.getSourceAccountId()).isEqualTo("ACC001");
    }
}
