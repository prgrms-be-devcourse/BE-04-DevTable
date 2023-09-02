package com.mdh.devtable.user.domain;

import com.mdh.devtable.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public User(Long id, String email, Role role, String password) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public void confirmPassword(String password) {
        if (!password.equals(this.password)) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }
    }
}
