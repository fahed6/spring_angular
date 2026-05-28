package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.DashboardResponse;
import com.javauit.autoecole.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin — Tableau de bord", description = "Statistiques globales pour le dashboard administrateur")
@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(
        summary = "Statistiques du tableau de bord",
        description = "Retourne les KPIs (étudiants, utilisateurs, nouveaux ce mois) ainsi que les données " +
                      "pour les 3 graphiques : répartition des étudiants, inscriptions mensuelles (6 derniers mois), " +
                      "et statut des séances de formation."
    )
    @ApiResponse(responseCode = "200", description = "Statistiques retournées avec succès")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
