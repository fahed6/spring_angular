package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Réponse d'authentification contenant le token JWT")
public record LoginResponse(
        @Schema(description = "Token JWT à utiliser dans le header Authorization: Bearer {token}")
        String token,

        @Schema(description = "Rôle du compte", example = "ADMIN", allowableValues = {"ADMIN", "STAFF"})
        String role,

        @Schema(description = "Nom complet de l'utilisateur", example = "Admin")
        String fullName) {}
