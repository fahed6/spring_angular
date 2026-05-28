package com.javauit.autoecole.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Saisir le token JWT obtenu via POST /api/auth/login");

        return new OpenAPI()
                .info(new Info()
                        .title("Auto École UIT — API")
                        .version("1.0.0")
                        .description("API REST de gestion d'une auto-école. " +
                                "Authentification via JWT. Deux rôles : ADMIN et STAFF.")
                        .contact(new Contact()
                                .name("Auto École UIT")
                                .email("admin@autoecole.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Serveur local")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme));
    }
}
