package com.bendigobank.commissionquote.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI commissionQuoteOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Commission Quote API")
                        .version("v1")
                        .description("Platform API consumed by the frontend, plus the mock "
                                + "vendor endpoint it calls internally. See /api/quotes for the "
                                + "frontend-facing contract and /vendor/commission-quotes for the "
                                + "vendor contract (server-to-server only, requires api-key)."))
                .servers(List.of(new Server()
                        .url("http://localhost:8081")
                        .description("Local development")))
                .components(new Components()
                        .addSecuritySchemes("api-key", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("api-key")
                                .description("Required only by /vendor/commission-quotes. Never "
                                        + "sent by the frontend - the platform API holds this "
                                        + "secret and attaches it server-side.")));
    }
}
