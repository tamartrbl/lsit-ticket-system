package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.*;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.oidc.user.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.*;

import java.util.List;
import java.util.Map;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/protected/**").authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .oidcUserService(oauth2UserService());

        return http.build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oauth2UserService() {
        return userRequest -> {
            OidcUser oidcUser = new DefaultOidcUser(
                    userRequest.getIdToken().getClaims(),
                    userRequest.getAccessToken()
            );

            // Fetch GitLab groups using the access token
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://gitlab.com/api/v4/groups",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> groups = response.getBody();

            // Check if user belongs to the required group
            boolean isInCorrectGroup = groups != null && groups.stream()
                    .anyMatch(group -> "LSIT Ticket System".equals(group.get("name")));

            if (!isInCorrectGroup) {
                throw new AccessDeniedException("User is not in the GitLab group.");
            }

            return oidcUser;
        };
    }
}