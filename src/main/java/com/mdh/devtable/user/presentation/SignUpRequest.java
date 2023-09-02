package com.mdh.devtable.user.presentation;

import com.mdh.devtable.user.domain.Role;
import com.mdh.devtable.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Email(message = "이메일 형식에 맞게 입력해 주세요.")
        String email,
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{8,}$",
                message = "비밀번호는 최소 8글자의 최소 1개 이상의 문자, 1개 이상의 숫자가 포함 되어야 합니다.")
        String password,
        @NotEmpty(message = "비밀번호 확인을 입력해 주세요.")
        String passwordCheck
) {
    public User toEntity() {
        return User.builder()
                .email(email)
                .password(password)
                .role(Role.GUEST).build();
    }
}