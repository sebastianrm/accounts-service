package com.yanki.accounts.domain.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TopicsTest {

    @Test
    void allTopicsAreDefined() {
        assertThat(Topics.ACCOUNT_OPENED).isNotNull().isNotEmpty();
        assertThat(Topics.ACCOUNT_CLOSED).isNotNull().isNotEmpty();
        assertThat(Topics.ACCOUNT_MOVEMENT_REGISTERED).isNotNull().isNotEmpty();
        assertThat(Topics.DEBIT_PAYMENT_SETTLED).isNotNull().isNotEmpty();
        assertThat(Topics.TRANSFER_SETTLED).isNotNull().isNotEmpty();
    }
}
