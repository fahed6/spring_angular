package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.StudentRequest;
import com.javauit.autoecole.dto.StudentResponse;
import com.javauit.autoecole.exception.GlobalExceptionHandler;
import com.javauit.autoecole.service.StudentService;
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

import java.security.Principal;
import java.util.List;

@Tag(name = "Admin — Étudiants", description = "Gestion complète des étudiants (ADMIN uniquement)")
@RestController
@RequestMapping("/api/admin/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Lister tous les étudiants", description = "Retourne la liste complète ou filtrée par recherche textuelle")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll(
            @Parameter(description = "Recherche sur le nom, prénom ou email") @RequestParam(required = false) String search) {
        return ResponseEntity.ok(studentService.getAll(search));
    }

    @Operation(summary = "Obtenir un étudiant par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(
            @Parameter(description = "Identifiant de l'étudiant", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @Operation(summary = "Créer un étudiant")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Étudiant créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody StudentRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(studentService.create(request, principal.getName()));
    }

    @Operation(summary = "Modifier un étudiant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Étudiant mis à jour"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> update(
            @Parameter(description = "Identifiant de l'étudiant", required = true) @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.update(id, request));
    }

    @Operation(summary = "Supprimer un étudiant")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Étudiant supprimé"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de l'étudiant", required = true) @PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
