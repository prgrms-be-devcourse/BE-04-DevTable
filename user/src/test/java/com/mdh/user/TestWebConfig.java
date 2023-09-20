package com.mdh.user;

import com.mdh.user.global.security.session.CurrentUserHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Profile("test")
@Configuration
@EnableWebMvc
public class TestWebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserHandlerMethodArgumentResolver());
    }

}