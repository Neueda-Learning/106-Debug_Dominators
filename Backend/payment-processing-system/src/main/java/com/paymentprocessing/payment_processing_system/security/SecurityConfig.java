package com.paymentprocessing.payment_processing_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        // CORS preflight - Public Access
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .permitAll()

                        // Swagger - Public Access
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        )
                        .permitAll()

                        // All Application APIs - Public Access
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
                        .permitAll()

                        // Everything else - Public Access
                        .anyRequest()
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
