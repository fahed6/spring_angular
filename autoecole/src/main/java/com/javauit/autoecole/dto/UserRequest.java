package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Données pour créer ou modifier un compte utilisateur système")
public record UserRequest(
        @NotBlank(message = "Le nom complet est obligatoire")
        @Schema(description = "Nom complet", example = "Sarah Mokrani")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format d'email invalide")
        @Schema(description = "Adresse email (identifiant de connexion)", example = "s.mokrani@autoecole.com")
        String email,

        @Schema(description = "Mot de passe (obligatoire à la création, optionnel à la modification)", example = "motdepasse123")
        String password,

        @Schema(description = "Rôle du compte", example = "STAFF", allowableValues = {"ADMIN", "STAFF"})
        String role,

        @Schema(description = "Compte actif ou désactivé", example = "true")
        Boolean isActive) {}
