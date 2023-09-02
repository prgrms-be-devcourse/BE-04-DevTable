package com.mdh.devtable.user.application;

import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.user.presentation.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입을 한다.")
    public void testSignUp() {
        // given
        var signUpRequest = new SignUpRequest("test@example.com", "password123", "password123");
        var expectedUser = signUpRequest.toEntity();

        // when
        userService.signUp(signUpRequest);

        // then
        var actualUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertThat(actualUser).isNotNull();
        assertThat(actualUser.getEmail()).isEqualTo(expectedUser.getEmail());
        assertThat(actualUser.getRole()).isEqualTo(expectedUser.getRole());
        assertThat(actualUser.getPassword()).isEqualTo(expectedUser.getPassword());
    }
}