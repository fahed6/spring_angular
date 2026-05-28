package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.PaymentRequest;
import com.javauit.autoecole.dto.PaymentResponse;
import com.javauit.autoecole.exception.GlobalExceptionHandler;
import com.javauit.autoecole.service.PaymentService;
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

@Tag(name = "Staff — Paiements", description = "Enregistrement et suivi des paiements des étudiants")
@RestController
@RequestMapping("/api/staff/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Lister les paiements", description = "Retourne tous les paiements ou filtrés par étudiant")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAll(
            @Parameter(description = "Filtrer par ID étudiant") @RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(paymentService.getAll(studentId));
    }

    @Operation(summary = "Obtenir un paiement par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
        @ApiResponse(responseCode = "404", description = "Paiement introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getById(
            @Parameter(description = "Identifiant du paiement", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getById(id));
    }

    @Operation(summary = "Enregistrer un paiement")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Paiement enregistré avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.create(request, principal.getName()));
    }

    @Operation(summary = "Modifier un paiement (statut, montant…)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Paiement mis à jour"),
        @ApiResponse(responseCode = "404", description = "Paiement introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponse> update(
            @Parameter(description = "Identifiant du paiement", required = true) @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.update(id, request));
    }

    @Operation(summary = "Supprimer un paiement")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Paiement supprimé"),
        @ApiResponse(responseCode = "404", description = "Paiement introuvable",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identifiant du paiement", required = true) @PathVariable Long id) {
        paymentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
