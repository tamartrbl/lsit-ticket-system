package com.example.Repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class Config {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/events/**").hasAnyRole("EventManager", "Customer")
                .requestMatchers("/notifications/**").hasRole("Marketing")
                .requestMatchers("/signups/**").hasRole("Customer")
                .anyRequest().authenticated()
        )
        .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserService())
                )
        );
        return http.build();
    }


    private OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return userRequest -> {
            OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

            Set<SimpleGrantedAuthority> authorities = oauth2User.getAuthorities().stream()
                    .map(authority -> {
                        String groupName = authority.getAuthority();
                        if ("Event".equalsIgnoreCase(groupName)) {
                            return new SimpleGrantedAuthority("ROLE_EventManager");
                        } else if ("Marketing".equalsIgnoreCase(groupName)) {
                            return new SimpleGrantedAuthority("ROLE_Marketing");
                        }
                        return null;
                    })
                    .filter(auth -> auth != null)
                    .collect(Collectors.toSet());

            if (authorities.isEmpty()) {
                authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_Customer"));
            }

            return new CustomOAuth2User(oauth2User, authorities);
        };
    }
}