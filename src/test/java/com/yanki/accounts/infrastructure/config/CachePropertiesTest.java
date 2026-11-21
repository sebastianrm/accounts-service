package com.yanki.accounts.infrastructure.config;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class CachePropertiesTest {

    @Test
    void defaultValues() {
        CacheProperties props = new CacheProperties();

        assertThat(props.getBalanceTtl()).isEqualTo(Duration.ofMinutes(5));
        assertThat(props.getKeyPrefix()).isEqualTo("yanki:accounts:");
    }

    @Test
    void setters() {
        CacheProperties props = new CacheProperties();
        props.setBalanceTtl(Duration.ofMinutes(10));
        props.setKeyPrefix("custom:");

        assertThat(props.getBalanceTtl()).isEqualTo(Duration.ofMinutes(10));
        assertThat(props.getKeyPrefix()).isEqualTo("custom:");
    }
}
