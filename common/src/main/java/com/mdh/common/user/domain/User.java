package com.mdh.common.user.domain;

import com.mdh.common.global.BaseTimeEntity;
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

    @Column(name = "phone_number", length = 15, nullable = false)
    private String phoneNumber;

    @Builder
    public User(String email, Role role, String password, String phoneNumber) {
        this.email = email;
        this.role = role;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}