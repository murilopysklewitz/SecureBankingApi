package com.SecureBankingApi.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Value("${spring.application.version:1.0.0}")
    private String version;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemaName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Secure Bank API")
                        .description("API RESTful for a Banking system with SpringBoot and JWT")
                        .version(version)
                        .contact(new Contact()
                                .email("murilopyskfuzikawa@gmail.com")
                                .name("Murilo Pysklewitz")
                                .url("https://github.com/murilopysklewitz")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemaName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemaName,
                                new SecurityScheme()
                                        .name(securitySchemaName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Insert the JWT token in login")));

    }
}
