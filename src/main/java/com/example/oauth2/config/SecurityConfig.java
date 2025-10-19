package com.example.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // allow home, error, h2-console and static resources
                .requestMatchers("/", "/login/**", "/error", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            )
            // H2 console needs frame options disabled and csrf turned off for its endpoints
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/profile", true)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
            );

        return http.build();
    }
}
