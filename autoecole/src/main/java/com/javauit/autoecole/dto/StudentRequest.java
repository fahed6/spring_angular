package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@Schema(description = "Données pour créer ou modifier un étudiant")
public record StudentRequest(
        @NotBlank(message = "Le prénom est obligatoire")
        @Schema(description = "Prénom", example = "Mohamed")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        @Schema(description = "Nom de famille", example = "Benali")
        String lastName,

        @Schema(description = "Adresse email", example = "m.benali@example.com")
        String email,

        @Schema(description = "Numéro de téléphone", example = "+213 555 123 456")
        String phone,

        @Schema(description = "Date de naissance (YYYY-MM-DD)", example = "2000-05-15")
        LocalDate dateOfBirth,

        @Schema(description = "Adresse postale", example = "12 Rue de la Paix, Alger")
        String address,

        @Schema(description = "Statut de l'étudiant", example = "ACTIVE",
                allowableValues = {"ACTIVE", "COMPLETED", "SUSPENDED"})
        String status) {}
