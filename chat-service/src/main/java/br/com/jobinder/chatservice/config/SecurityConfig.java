package br.com.jobinder.chatservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/ws/chat/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/conversations/**",
                                "/api/v1/messages/**"
                        ).permitAll()

                        // Needs authentication for any other requests
                        .anyRequest().authenticated()
                )
                // TODO: JWT token filter
                .build();
    }
}