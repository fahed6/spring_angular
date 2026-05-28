package com.javauit.autoecole.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Données pour enregistrer ou modifier un paiement")
public record PaymentRequest(
        @NotNull(message = "L'identifiant de l'étudiant est obligatoire")
        @Schema(description = "ID de l'étudiant", example = "1")
        Long studentId,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
        @Schema(description = "Montant du paiement (DZD)", example = "15000.00")
        BigDecimal amount,

        @Schema(description = "Date du paiement (YYYY-MM-DD)", example = "2026-06-01")
        LocalDate paidAt,

        @Schema(description = "Moyen de paiement", example = "CASH",
                allowableValues = {"CASH", "CARD", "TRANSFER"})
        String method,

        @Schema(description = "Statut du paiement", example = "PAID",
                allowableValues = {"PAID", "PENDING", "PARTIAL"})
        String status,

        @Schema(description = "Description ou référence", example = "Acompte formation conduite")
        String description) {}
