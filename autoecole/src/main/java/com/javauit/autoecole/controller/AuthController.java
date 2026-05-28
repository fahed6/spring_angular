package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.LoginRequest;
import com.javauit.autoecole.dto.LoginResponse;
import com.javauit.autoecole.exception.GlobalExceptionHandler;
import com.javauit.autoecole.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentification", description = "Connexion et obtention du token JWT")
@RestController
@RequestMapping("/api/auth")
@SecurityRequirement(name = "")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        summary = "Connexion",
        description = "Authentifie un utilisateur et retourne un token JWT valable 24h. " +
                      "Utiliser ce token dans le bouton 'Authorize' de Swagger UI (valeur : Bearer {token})."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Connexion réussie — token JWT retourné",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Identifiants invalides ou compte désactivé",
            content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorBody.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
