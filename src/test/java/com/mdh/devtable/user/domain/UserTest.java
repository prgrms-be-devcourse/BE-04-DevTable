package com.mdh.devtable.user.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("비밀번호를 정상적으로 확인한다.")
    void testConfirmPassword_MatchingPasswords() {
        // given
        var user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.GUEST)
                .password("password123")
                .build();

        var confirmPassword = "password123";

        // when & then
        // No exception should be thrown
        assertDoesNotThrow(() -> user.confirmPassword(confirmPassword));

        // then
        // If no exception is thrown, the test passes
    }

    @Test
    @DisplayName("비밀번호 확인이 알맞지 않으면 예외를 던진다")
    void testConfirmPassword_NonMatchingPasswords() {
        // given
        var user = User.builder()
                .id(1L)
                .email("test@example.com")
                .role(Role.GUEST)
                .password("password123")
                .build();

        var confirmPassword = "wrongPassword";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> user.confirmPassword(confirmPassword));
    }
}
