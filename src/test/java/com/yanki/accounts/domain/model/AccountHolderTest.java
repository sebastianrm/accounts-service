package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHolderTest {

    @Test
    void builder_setsAllFields() {
        AccountHolder holder = AccountHolder.builder()
                .holderId("H001")
                .percentage(50)
                .principal(true)
                .build();

        assertThat(holder.getHolderId()).isEqualTo("H001");
        assertThat(holder.getPercentage()).isEqualTo(50);
        assertThat(holder.getPrincipal()).isTrue();
    }
}
