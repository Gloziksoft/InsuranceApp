package com.insurance.app.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApplicationSecurityConfiguration {

    // Configures HTTP security rules and authentication mechanisms
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests()

                .requestMatchers("/insurance/*/events/reports/**").hasRole("ADMIN")

                // Nový poistenec - len admin
                .requestMatchers("/insured-persons/create").hasRole("ADMIN")

                // Bežný prístup k zoznamu, detailom, editácii a mazaniu - USER aj ADMIN
                .requestMatchers(
                        "/insured-persons",
                        "/insured-persons/detail/**",
                        "/insured-persons/edit/**",
                        "/insured-persons/delete/**"
                ).hasAnyRole("USER", "ADMIN")

                // Verejné stránky
                .requestMatchers(
                        "/styles/**", "/images/**", "/scripts/**", "/fonts/**",
                        "/about-us", "/", "/account/register", "/account/login"
                ).permitAll()

                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/account/login")
                .loginProcessingUrl("/account/login")
                .defaultSuccessUrl("/insured-persons", true)
                .usernameParameter("email")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/account/logout")
                .permitAll()

                .and().build();
    }

    // Provides a password encoder bean using BCrypt hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
