package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountSignerTest {

    @Test
    void builder_setsAllFields() {
        AccountSigner signer = AccountSigner.builder()
                .signerId("S001")
                .operationLimit(new java.math.BigDecimal("1000.00"))
                .active(true)
                .build();

        assertThat(signer.getSignerId()).isEqualTo("S001");
        assertThat(signer.getOperationLimit()).isEqualByComparingTo("1000.00");
        assertThat(signer.getActive()).isTrue();
    }
}
