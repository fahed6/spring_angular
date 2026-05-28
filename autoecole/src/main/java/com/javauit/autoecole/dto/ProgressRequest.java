package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Données pour mettre à jour la progression d'un étudiant")
public record ProgressRequest(
        @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
        @Schema(description = "ID de l'étudiant", example = "1")
        Long studentId,

        @Schema(description = "Catégorie de progression", example = "CODE",
                allowableValues = {"CODE", "CONDUITE", "GENERAL"})
        String category,

        @Schema(description = "Heures de formation effectuées", example = "12.5")
        BigDecimal hoursCompleted,

        @Schema(description = "Heures de formation requises au total", example = "20.0")
        BigDecimal hoursRequired,

        @Min(value = 0, message = "Le score doit être entre 0 et 100")
        @Max(value = 100, message = "Le score doit être entre 0 et 100")
        @Schema(description = "Score obtenu (0-100)", example = "78")
        Integer score) {}
