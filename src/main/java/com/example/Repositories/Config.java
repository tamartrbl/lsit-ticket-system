package com.example.Repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class Config {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/events/**").hasAnyRole("EventManager", "Customer")
                .requestMatchers("/notifications/**").hasRole("Marketing")
                .requestMatchers("/customers/**").hasRole("Customer")
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

            // Debugging: Print all attributes
            System.out.println("Attributes: " + oauth2User.getAttributes());

            // Extract groups from attributes
            @SuppressWarnings("unchecked")
            List<String> groups = (List<String>) oauth2User.getAttributes().get("groups");
            if (groups == null) groups = Collections.emptyList();

            // Debugging: Print the groups
            System.out.println("Groups: " + groups);

            // Convert groups to roles
            Set<SimpleGrantedAuthority> authorities = groups.stream()
                    .map(group -> {
                        if (group.equals("lsit-ken3239/roles/ticket_system/ticket_event_manager")) {
                            System.out.println("Matched group: EventManager");
                            return new SimpleGrantedAuthority("ROLE_EventManager");
                        } else if (group.equals("lsit-ken3239/roles/ticket_system/ticket_marketing")) {
                            System.out.println("Matched group: Marketing");
                            return new SimpleGrantedAuthority("ROLE_Marketing");
                        } else if (group.equals("lsit-ken3239/roles/ticket_system/ticket_customer")) {
                            System.out.println("Matched group: Customer");
                            return new SimpleGrantedAuthority("ROLE_Customer");
                        } else {
                            // Debugging: Print unmatched groups
                            System.out.println("Unmatched group: " + group);
                        }
                        return null;
                    })
                    .filter(auth -> auth != null)
                    .collect(Collectors.toSet());

            // Assign default role if no group matches
            if (authorities.isEmpty()) {
                System.out.println("No matching groups found. Assigning default role: Customer.");
                authorities.add(new SimpleGrantedAuthority("ROLE_Customer"));
            }

            // Debugging: Print final authorities
            System.out.println("Final Authorities: " + authorities);

            // Return CustomOAuth2User with mapped authorities
            return new CustomOAuth2User(oauth2User, authorities);
        };
    }
}