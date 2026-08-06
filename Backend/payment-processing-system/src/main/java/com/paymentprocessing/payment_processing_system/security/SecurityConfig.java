package com.paymentprocessing.payment_processing_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        // Swagger - Public Access
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        )
                        .permitAll()


                        // All Application APIs - Protected
                        .requestMatchers(
                                "/payments/**",
                                "/refunds/**",
                                "/crypto-payments/**",
                                "/campaigns/**",
                                "/contributions/**",
                                "/payment-history/**",
                                "/notifications/**",
                                "/audit-logs/**",
                                "/statements/**",
                                "/retry/**",
                                "/exchange/**"
                        )
                        .authenticated()


                        // Everything else requires authentication
                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(Customizer.withDefaults());


        return http.build();
    }
}