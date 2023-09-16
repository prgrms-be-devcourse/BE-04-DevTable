package com.mdh.user;

import com.mdh.common.user.domain.Role;
import com.mdh.common.user.persistence.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QueryDslTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findUserByRoleTest() {
        //given
        var guest = DataInitializerFactory.guest();
        userRepository.save(guest);

        //when
        var findUsers = userRepository.findByRole(Role.GUEST);

        //then
        Assertions.assertThat(findUsers).hasSize(1);
    }
}
