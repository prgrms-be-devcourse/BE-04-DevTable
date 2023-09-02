package com.mdh.devtable.user.application;

import com.mdh.devtable.user.infra.persistence.UserRepository;
import com.mdh.devtable.user.presentation.SignUpRequest;
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
        user.confirmPassword(signUpRequest.passwordCheck());
        return userRepository.save(user).getId();
    }
}