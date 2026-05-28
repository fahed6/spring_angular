package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.PlanningRequest;
import com.javauit.autoecole.dto.PlanningResponse;
import com.javauit.autoecole.exception.GlobalExceptionHandler;
import com.javauit.autoecole.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Staff — Planning", description = "Planification et suivi des séances de formation")
@RestController
@RequestMapping("/api/staff/planning")
public class PlanningController {

    private final PlanningService planningService;

    public PlanningController(PlanningService planningService) {
        this.planningService = planningService;
    }

    @Operation(summary = "Lister les séances", description = "Retourne toutes les séances ou filtrées par étudiant")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<PlanningResponse>> getAll(
            @Parameter(description = "Filtrer par ID étudiant") @RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(planningService.getAll(studentId));
    }

    @Operation(summary = "Obtenir une séance par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Séance trouvée"),
        @ApiResponse(responseCode = "404", description = "Séance introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PlanningResponse> getById(
            @Parameter(description = "Identifiant de la séance", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(planningService.getById(id));
    }

    @Operation(summary = "Planifier une séance")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Séance créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PostMapping
    public ResponseEntity<PlanningResponse> create(@Valid @RequestBody PlanningRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(planningService.create(request));
    }

    @Operation(summary = "Modifier une séance (statut, notes, date…)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Séance mise à jour"),
        @ApiResponse(responseCode = "404", description = "Séance introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PlanningResponse> update(
            @Parameter(description = "Identifiant de la séance", required = true) @PathVariable Long id,
            @Valid @RequestBody PlanningRequest request) {
        return ResponseEntity.ok(planningService.update(id, request));
    }

    @Operation(summary = "Supprimer une séance")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Séance supprimée"),
        @ApiResponse(responseCode = "404", description = "Séance introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de la séance", required = true) @PathVariable Long id) {
        planningService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
