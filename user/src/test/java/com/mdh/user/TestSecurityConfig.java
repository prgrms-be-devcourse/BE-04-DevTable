package com.mdh.user;

import com.mdh.user.global.security.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Profile("test")
@EnableWebSecurity
@Configuration
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, LoginService loginService) throws Exception {

        var auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(loginService);

        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
                .formLogin((formLogin) -> formLogin
                        .defaultSuccessUrl("/")
                        .usernameParameter("username")
                        .passwordParameter("password")//html 로그인 페이지에 username, pawssword에 해당하는 파라미터 값(아이디랑 비밀번호)
                        .permitAll()
                )

                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("remember-me")
                )
                .rememberMe((rememberMe) -> rememberMe
                        .key("my-remember-me")
                        .rememberMeParameter("remember-me")//html 로그인 페이지에 name에 해당하는 파라미터 값
                        .tokenValiditySeconds(300))

                .sessionManagement((sessionManagement -> sessionManagement
                        .sessionFixation().changeSessionId() // session fixation 방지
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 생성 전략
                        .invalidSessionUrl("/") // 유효하지 않은 세션에서 요청시 리다이렉트 되는 url
                        .maximumSessions(1) // 1개의 로그인만 가능
                        .maxSessionsPreventsLogin(false) // 새로운 로그인 발생시 기존 로그인이 아닌 새로운 로그인 허용
                )).build();
    }
}