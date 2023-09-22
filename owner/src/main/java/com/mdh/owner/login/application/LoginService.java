package com.mdh.owner.login.application;

import com.mdh.common.user.persistence.UserRepository;
import com.mdh.owner.login.presentation.SignUpRequest;
import com.mdh.owner.global.security.session.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 유저입니다.: " + username));

        return CustomUser.withUsername(user.getEmail())
                .userId(user.getId())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    @Transactional
    public Long signUp(SignUpRequest signUpRequest) {
        if (!signUpRequest.password().equals(signUpRequest.passwordCheck())) {
            throw new IllegalArgumentException("비밀번호 확인을 제대로 입력해 주세요");
        }
        return userRepository.save(signUpRequest.toEntity(passwordEncoder)).getId();
    }
}