package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRiskViewTest {

    @Test
    void builder_setsAllFields() {
        CustomerRiskView view = CustomerRiskView.builder()
                .customerId("CUST001")
                .hasOverdueDebt(true)
                .overdueAmount(new BigDecimal("500.00"))
                .build();

        assertThat(view.getCustomerId()).isEqualTo("CUST001");
        assertThat(view.getHasOverdueDebt()).isTrue();
        assertThat(view.getOverdueAmount()).isEqualTo(new BigDecimal("500.00"));
    }

    @Test
    void builder_noOverdueDebt() {
        CustomerRiskView view = CustomerRiskView.builder()
                .customerId("CUST001")
                .hasOverdueDebt(false)
                .overdueAmount(BigDecimal.ZERO)
                .build();

        assertThat(view.getHasOverdueDebt()).isFalse();
    }
}
