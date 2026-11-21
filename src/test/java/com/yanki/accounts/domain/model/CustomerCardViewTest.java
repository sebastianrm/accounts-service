package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerCardViewTest {

    @Test
    void builder_setsAllFields() {
        CustomerCardView view = CustomerCardView.builder()
                .customerId("CUST001")
                .creditCardId("CC001")
                .hasCreditCard(true)
                .build();

        assertThat(view.getCustomerId()).isEqualTo("CUST001");
        assertThat(view.getCreditCardId()).isEqualTo("CC001");
        assertThat(view.getHasCreditCard()).isTrue();
    }
}
