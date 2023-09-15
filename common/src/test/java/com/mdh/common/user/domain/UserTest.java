package com.mdh.common.user.domain;

import com.mdh.common.DataInitializerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("유저를 생성할 수 있다.")
    void couldCreateUser() {
        // given & when
        var user = DataInitializerFactory.guest();

        // then
        Assertions.assertThat(user).isNotNull();
    }
}