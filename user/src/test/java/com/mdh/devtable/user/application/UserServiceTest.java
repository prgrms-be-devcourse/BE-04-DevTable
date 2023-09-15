package com.mdh.devtable.user.application;

import com.mdh.devtable.user.domain.User;
import com.mdh.devtable.user.persistence.UserRepository;
import com.mdh.devtable.user.presentation.dto.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입을 한다.")
    public void testSignUp() {
        // given
        var signUpRequest = new SignUpRequest("test@example.com", "password123", "password123", "01012345678");
        var expectedUser = signUpRequest.toEntity();
        given(userRepository.save(any(User.class))).willReturn(expectedUser);

        // when
        userService.signUp(signUpRequest);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }
}