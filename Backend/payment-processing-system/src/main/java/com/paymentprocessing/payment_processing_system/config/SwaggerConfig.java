package com.paymentprocessing.payment_processing_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {


    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title("Payment Processing System API")
                                .version("1.0.0")
                                .description(
                                        "REST API documentation for Payment Processing System developed by Debug Dominators"
                                )
                                .contact(
                                        new Contact()
                                                .name("Debug Dominators")
                                )
                );
    }
}