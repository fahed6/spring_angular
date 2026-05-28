package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Données de connexion")
public record LoginRequest(
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        @Schema(description = "Adresse email du compte", example = "admin@autoecole.com")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Schema(description = "Mot de passe", example = "admin123")
        String password) {}
