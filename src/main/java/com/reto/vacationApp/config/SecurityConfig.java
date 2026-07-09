package com.reto.vacationApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/employee/**").hasRole("EMPLOYEE")
                .requestMatchers("/api/boss/**").hasRole("BOSS")
                .requestMatchers("/api/hr/**").hasRole("HR")
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        UserDetails employee = User.builder()
                .username("employee1")
                .password("{noop}pass")
                .roles("EMPLOYEE")
                .build();
        UserDetails boss = User.builder()
                .username("boss1")
                .password("{noop}pass")
                .roles("BOSS")
                .build();
        UserDetails hr = User.builder()
                .username("hr1")
                .password("{noop}pass")
                .roles("HR")
                .build();

        return new InMemoryUserDetailsManager(employee, boss, hr);
    }
}