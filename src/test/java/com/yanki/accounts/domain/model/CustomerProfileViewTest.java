package com.yanki.accounts.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerProfileViewTest {

    @Test
    void canOperate_whenActive_returnsTrue() {
        CustomerProfileView view = CustomerProfileView.builder()
                .status("ACTIVE")
                .build();

        assertThat(view.canOperate()).isTrue();
    }

    @Test
    void canOperate_whenBlocked_returnsFalse() {
        CustomerProfileView view = CustomerProfileView.builder()
                .status("BLOCKED")
                .build();

        assertThat(view.canOperate()).isFalse();
    }

    @Test
    void canOperate_whenDeleted_returnsFalse() {
        CustomerProfileView view = CustomerProfileView.builder()
                .status("DELETED")
                .build();

        assertThat(view.canOperate()).isFalse();
    }

    @Test
    void isPersonal_whenPersonal_returnsTrue() {
        CustomerProfileView view = CustomerProfileView.builder()
                .customerType("PERSONAL")
                .build();

        assertThat(view.isPersonal()).isTrue();
    }

    @Test
    void isPersonal_whenEnterprise_returnsFalse() {
        CustomerProfileView view = CustomerProfileView.builder()
                .customerType("ENTERPRISE")
                .build();

        assertThat(view.isPersonal()).isFalse();
    }

    @Test
    void isEnterprise_whenEnterprise_returnsTrue() {
        CustomerProfileView view = CustomerProfileView.builder()
                .customerType("ENTERPRISE")
                .build();

        assertThat(view.isEnterprise()).isTrue();
    }

    @Test
    void isEnterprise_whenPersonal_returnsFalse() {
        CustomerProfileView view = CustomerProfileView.builder()
                .customerType("PERSONAL")
                .build();

        assertThat(view.isEnterprise()).isFalse();
    }

    @Test
    void isVip_whenVip_returnsTrue() {
        CustomerProfileView view = CustomerProfileView.builder()
                .profile("VIP")
                .build();

        assertThat(view.isVip()).isTrue();
    }

    @Test
    void isVip_whenStandard_returnsFalse() {
        CustomerProfileView view = CustomerProfileView.builder()
                .profile("STANDARD")
                .build();

        assertThat(view.isVip()).isFalse();
    }

    @Test
    void isPyme_whenPyme_returnsTrue() {
        CustomerProfileView view = CustomerProfileView.builder()
                .profile("PYME")
                .build();

        assertThat(view.isPyme()).isTrue();
    }

    @Test
    void isPyme_whenStandard_returnsFalse() {
        CustomerProfileView view = CustomerProfileView.builder()
                .profile("STANDARD")
                .build();

        assertThat(view.isPyme()).isFalse();
    }
}
