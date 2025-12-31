package com.insurance.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurityConfiguration {

    /**
     * ===============================
     * 🔓 ACTUATOR SECURITY (NO AUTH)
     * ===============================
     * - musí byť PRVÝ filter chain
     * - žiadne login redirecty
     * - žiadne CSRF
     * - žiadne session
     */
    @Bean
    @Order(1)
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/actuator/**")
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    /**
     * ===============================
     * 🔐 APPLICATION SECURITY
     * ===============================
     */
    @Bean
    @Order(2)
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/insurance/*/events/reports/**").hasRole("ADMIN")

                        // Nový poistenec - len admin
                        .requestMatchers("/insured-persons/create").hasRole("ADMIN")

                        // Bežný prístup - USER + ADMIN
                        .requestMatchers(
                                "/insured-persons",
                                "/insured-persons/detail/**",
                                "/insured-persons/edit/**",
                                "/insured-persons/delete/**"
                        ).hasAnyRole("USER", "ADMIN")

                        // Verejné stránky
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
                );

        return http.build();
    }

    /**
     * ===============================
     * 🔑 PASSWORD ENCODER
     * ===============================
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
