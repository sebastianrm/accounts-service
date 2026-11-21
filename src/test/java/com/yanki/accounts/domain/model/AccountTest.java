package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    @Test
    void canOperate_whenOpen_returnsTrue() {
        Account account = Account.builder()
                .status(AccountStatus.OPEN)
                .build();

        assertThat(account.canOperate()).isTrue();
    }

    @Test
    void canOperate_whenClosed_returnsFalse() {
        Account account = Account.builder()
                .status(AccountStatus.CLOSED)
                .build();

        assertThat(account.canOperate()).isFalse();
    }

    @Test
    void canOperate_whenBlocked_returnsFalse() {
        Account account = Account.builder()
                .status(AccountStatus.BLOCKED)
                .build();

        assertThat(account.canOperate()).isFalse();
    }

    @Test
    void incrementMovementCount_sameMonth_incrementsCounter() {
        LocalDateTime now = LocalDateTime.of(2026, 11, 5, 10, 0);
        Account account = Account.builder()
                .currentMonth("2026-11")
                .monthlyMovementCount(3)
                .build();

        account.incrementMovementCount(now);

        assertThat(account.getMonthlyMovementCount()).isEqualTo(4);
        assertThat(account.getCurrentMonth()).isEqualTo("2026-11");
    }

    @Test
    void incrementMovementCount_newMonth_resetsCounter() {
        LocalDateTime now = LocalDateTime.of(2026, 12, 1, 10, 0);
        Account account = Account.builder()
                .currentMonth("2026-11")
                .monthlyMovementCount(10)
                .build();

        account.incrementMovementCount(now);

        assertThat(account.getMonthlyMovementCount()).isEqualTo(1);
        assertThat(account.getCurrentMonth()).isEqualTo("2026-12");
    }

    @Test
    void hasExceededFreeMovements_whenExceeded_returnsTrue() {
        Account account = Account.builder()
                .maxFreeMovementsPerMonth(10)
                .monthlyMovementCount(11)
                .build();

        assertThat(account.hasExceededFreeMovements()).isTrue();
    }

    @Test
    void hasExceededFreeMovements_whenNotExceeded_returnsFalse() {
        Account account = Account.builder()
                .maxFreeMovementsPerMonth(10)
                .monthlyMovementCount(5)
                .build();

        assertThat(account.hasExceededFreeMovements()).isFalse();
    }

    @Test
    void hasExceededFreeMovements_whenNullLimit_returnsFalse() {
        Account account = Account.builder()
                .maxFreeMovementsPerMonth(null)
                .monthlyMovementCount(100)
                .build();

        assertThat(account.hasExceededFreeMovements()).isFalse();
    }

    @Test
    void hasExceededFreeMovements_whenNullCount_returnsFalse() {
        Account account = Account.builder()
                .maxFreeMovementsPerMonth(10)
                .monthlyMovementCount(null)
                .build();

        assertThat(account.hasExceededFreeMovements()).isFalse();
    }

    @Test
    void builder_setsAllFields() {
        Account account = Account.builder()
                .id("ACC001")
                .customerId("CUST001")
                .accountNumber("ACC1234567890123456")
                .type(AccountType.SAVINGS)
                .status(AccountStatus.OPEN)
                .currency("PEN")
                .balance(new BigDecimal("1000.00"))
                .minOpeningAmount(BigDecimal.ZERO)
                .maxFreeMovementsPerMonth(10)
                .commissionPerExcessMovement(new BigDecimal("2.50"))
                .maintenanceCommission(BigDecimal.ZERO)
                .openedAt(LocalDateTime.now())
                .build();

        assertThat(account.getId()).isEqualTo("ACC001");
        assertThat(account.getCustomerId()).isEqualTo("CUST001");
        assertThat(account.getType()).isEqualTo(AccountType.SAVINGS);
        assertThat(account.getStatus()).isEqualTo(AccountStatus.OPEN);
        assertThat(account.getBalance()).isEqualTo(new BigDecimal("1000.00"));
    }
}
