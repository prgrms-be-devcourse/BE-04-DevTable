package com.mdh.devtable.user.domain;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("유저를 생성할 수 있다.")
    void couldCreateUser() {
        // given

        // when
        var user = User.builder()
                .email("test@example.com")
                .role(Role.GUEST)
                .password("password123")
                .build();

        // then
        Assertions.assertThat(user).isNotNull();
    }
}