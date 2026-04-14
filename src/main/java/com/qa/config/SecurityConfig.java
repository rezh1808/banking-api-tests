package com.qa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth
          
            .requestMatchers("/h2-console/**", "/ui/register", "/auth/**").permitAll()
           
            .requestMatchers("/ui/verify").authenticated() 

            .anyRequest().authenticated()
        )
        // Permitting the login form so users can actually log in
        .formLogin(form -> form
            .loginPage("/ui/login") // If you have a custom login page
            .permitAll()
        )
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
}
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
