package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Données pour créer ou modifier une séance planifiée")
public record PlanningRequest(
        @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
        @Schema(description = "ID de l'étudiant", example = "1")
        Long studentId,

        @Schema(description = "ID de l'instructeur (optionnel)", example = "2")
        Long instructorId,

        @NotNull(message = "La date et heure de la séance sont obligatoires")
        @Schema(description = "Date et heure de la séance (ISO-8601)", example = "2026-06-15T09:00:00")
        LocalDateTime scheduledAt,

        @NotNull(message = "Le type de séance est obligatoire")
        @Schema(description = "Type de séance", example = "CODE", allowableValues = {"CODE", "CONDUITE"})
        String type,

        @Schema(description = "Statut de la séance", example = "SCHEDULED",
                allowableValues = {"SCHEDULED", "COMPLETED", "CANCELLED"})
        String status,

        @Schema(description = "Notes ou observations", example = "Séance de révision code de la route")
        String notes) {}
