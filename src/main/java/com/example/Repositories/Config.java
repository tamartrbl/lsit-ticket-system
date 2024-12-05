package com.example.Repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Config{

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(c -> c.disable())
                .oauth2Login(withDefaults())
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/authorise").authenticated()
                        .requestMatchers(HttpMethod.POST, "/events").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/events").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/events").authenticated()
                        .anyRequest().permitAll()
                )
        ;

        return http.build();
    }

}
