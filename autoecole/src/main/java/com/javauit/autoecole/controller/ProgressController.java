package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.ProgressRequest;
import com.javauit.autoecole.dto.ProgressResponse;
import com.javauit.autoecole.exception.GlobalExceptionHandler;
import com.javauit.autoecole.service.ProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Staff — Progression", description = "Suivi de la progression théorique et pratique des étudiants")
@RestController
@RequestMapping("/api/staff/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    @Operation(summary = "Obtenir la progression d'un étudiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Données de progression retournées"),
        @ApiResponse(responseCode = "400", description = "Paramètre studentId manquant",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @GetMapping
    public ResponseEntity<List<ProgressResponse>> getByStudent(
            @Parameter(description = "ID de l'étudiant", required = true) @RequestParam Long studentId) {
        return ResponseEntity.ok(progressService.getByStudent(studentId));
    }

    @Operation(summary = "Créer ou mettre à jour la progression",
               description = "Opération upsert : crée un enregistrement de progression s'il n'existe pas, le met à jour sinon.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Progression mise à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PostMapping
    public ResponseEntity<ProgressResponse> upsert(@Valid @RequestBody ProgressRequest request, Principal principal) {
        return ResponseEntity.ok(progressService.upsert(request, principal.getName()));
    }

    @Operation(summary = "Supprimer un enregistrement de progression")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Enregistrement supprimé"),
        @ApiResponse(responseCode = "404", description = "Enregistrement introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de l'enregistrement", required = true) @PathVariable Long id) {
        progressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
