package com.yanki.accounts.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void constructor_setsCodeAndMessage() {
        BusinessException ex = new BusinessException(ErrorCodes.ACCOUNT_NOT_FOUND, "Cuenta no encontrada");

        assertThat(ex.getCode()).isEqualTo(ErrorCodes.ACCOUNT_NOT_FOUND);
        assertThat(ex.getMessage()).isEqualTo("Cuenta no encontrada");
    }

    @Test
    void isRuntimeException() {
        BusinessException ex = new BusinessException("CODE", "Message");

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }
}
