package com.mdh.user.user.application;

import com.mdh.common.user.persistence.UserRepository;
import com.mdh.user.user.presentation.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long signUp(SignUpRequest signUpRequest) {
        var user = signUpRequest.toEntity();
        return userRepository.save(user).getId();
    }
}