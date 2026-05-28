package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.StaffDashboardResponse;
import com.javauit.autoecole.service.StaffDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Staff — Tableau de bord", description = "Statistiques opérationnelles pour le dashboard staff")
@RestController
@RequestMapping("/api/staff/dashboard")
public class StaffDashboardController {

    private final StaffDashboardService staffDashboardService;

    public StaffDashboardController(StaffDashboardService staffDashboardService) {
        this.staffDashboardService = staffDashboardService;
    }

    @Operation(
        summary = "Statistiques du tableau de bord staff",
        description = "Retourne les KPIs opérationnels et les données pour les 3 graphiques : " +
                      "statut des séances, statut des paiements, et répartition Code/Conduite."
    )
    @ApiResponse(responseCode = "200", description = "Statistiques retournées avec succès")
    @GetMapping
    public ResponseEntity<StaffDashboardResponse> getDashboard() {
        return ResponseEntity.ok(staffDashboardService.getStats());
    }
}
