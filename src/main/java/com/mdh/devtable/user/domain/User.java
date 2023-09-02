package com.mdh.devtable.user.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", length = 63, nullable = false)
    private String email;

    @Column(name = "role", length = 15, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @Builder
    public User(String email, Role role, String password) {        
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public void confirmPassword(String password) {
        if (!password.equals(this.password)) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }
    }
}