package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.UserRequest;
import com.javauit.autoecole.dto.UserResponse;
import com.javauit.autoecole.exception.GlobalExceptionHandler;
import com.javauit.autoecole.service.UserManagementService;
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

@Tag(name = "Admin — Utilisateurs système", description = "Gestion des comptes ADMIN et STAFF (ADMIN uniquement)")
@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @Operation(summary = "Lister tous les utilisateurs système")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userManagementService.getAll());
    }

    @Operation(summary = "Obtenir un utilisateur par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(
            @Parameter(description = "Identifiant de l'utilisateur", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getById(id));
    }

    @Operation(summary = "Créer un compte utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compte créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Email déjà utilisé ou données invalides",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userManagementService.create(request));
    }

    @Operation(summary = "Modifier un compte utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compte mis à jour"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "Identifiant de l'utilisateur", required = true) @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userManagementService.update(id, request));
    }

    @Operation(summary = "Supprimer un compte utilisateur")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compte supprimé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant de l'utilisateur", required = true) @PathVariable Long id) {
        userManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activer / désactiver un compte",
               description = "Bascule l'état is_active du compte. Un compte désactivé ne peut plus se connecter.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statut du compte modifié"),
        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<UserResponse> toggleActive(
            @Parameter(description = "Identifiant de l'utilisateur", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.toggleActive(id));
    }
}
