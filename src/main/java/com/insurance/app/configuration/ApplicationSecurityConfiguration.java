package com.insurance.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class ApplicationSecurityConfiguration {

    // ===============================
    // 🔓 ACTUATOR SECURITY
    // ===============================
    @Bean
    @Order(0)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(new AntPathRequestMatcher("/actuator/**"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    // ===============================
    // 🔐 APPLICATION SECURITY
    // ===============================
    @Bean
    @Order(1)
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/insurance/*/events/reports/**").hasRole("ADMIN")
                        .requestMatchers("/insured-persons/create").hasRole("ADMIN")

                        .requestMatchers(
                                "/insured-persons",
                                "/insured-persons/detail/**",
                                "/insured-persons/edit/**",
                                "/insured-persons/delete/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/styles/**",
                                "/images/**",
                                "/scripts/**",
                                "/fonts/**",
                                "/about-us",
                                "/",
                                "/account/register",
                                "/account/login"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/account/login")
                        .loginProcessingUrl("/account/login")
                        .defaultSuccessUrl("/insured-persons", true)
                        .usernameParameter("email")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/account/logout")
                        .permitAll()
                )
                // 🔑 KRITICKÉ PRE REVERSE PROXY
                .csrf(csrf -> csrf.disable())
                .build();
    }

    // ===============================
    // 🔑 PASSWORD ENCODER
    // ===============================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
