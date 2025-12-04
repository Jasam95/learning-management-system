package com.example.learning_management_system.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private UserDetailsService userDetailsService;
    private CustomSuccessHandler successHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Public pages
                        .requestMatchers("/", "/index", "/register", "/register/**",
                                "/static/**", "/css/**", "/js/**", "/images/**", "/posters/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/**").permitAll()

                        // Only students
                        .requestMatchers("/students/**").hasRole("STUDENT")

                        // Only ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        //Only instructor
                        .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                 // thymeleaf login
                        .loginProcessingUrl("/login")      // post login handle by spring security
                        .successHandler(successHandler)    // redirect to users (ADMIN, INSTRUCTOR, STUDENT)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return httpSecurity.build();
    }
}


