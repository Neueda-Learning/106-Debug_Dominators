package com.paymentprocessing.payment_processing_system.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
public class UserConfig {


    @Bean
    public UserDetailsService userDetailsService() {


        UserDetails user =
                User.builder()
                        .username("debug")
                        .password("{noop}n3u3da!")
                        .roles("USER")
                        .build();


        return new InMemoryUserDetailsManager(user);
    }
}