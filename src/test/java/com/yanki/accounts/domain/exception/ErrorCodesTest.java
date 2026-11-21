package com.yanki.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodesTest {

    @Test
    void allCodesAreNotNull() {
        assertThat(ErrorCodes.ACCOUNT_NOT_FOUND).isNotNull();
        assertThat(ErrorCodes.ACCOUNT_INSUFFICIENT_BALANCE).isNotNull();
        assertThat(ErrorCodes.ACCOUNT_CANNOT_CLOSE_BALANCE_NOT_ZERO).isNotNull();
        assertThat(ErrorCodes.CUSTOMER_NOT_FOUND).isNotNull();
        assertThat(ErrorCodes.ACCOUNT_BLOCKED).isNotNull();
        assertThat(ErrorCodes.CREDIT_CARD_REQUIRED_FOR_VIP).isNotNull();
        assertThat(ErrorCodes.DEBIT_PAYMENT_REJECTED).isNotNull();
        assertThat(ErrorCodes.VALIDATION_ERROR).isNotNull();
        assertThat(ErrorCodes.CUSTOMER_HAS_OVERDUE_DEBT).isNotNull();
    }
}
