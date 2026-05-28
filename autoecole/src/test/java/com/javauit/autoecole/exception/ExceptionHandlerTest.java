package com.javauit.autoecole.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cas d'utilisation — Structure des réponses d'erreur")
class ExceptionHandlerTest {

    // ─────────────────────────────────────────────────────────────────────────
    // UC-ERR-01 : ResourceNotFoundException → code RESOURCE_NOT_FOUND
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-ERR-01 : ResourceNotFoundException(resource, id) → message formaté")
    void resourceNotFoundException_withResourceAndId_formatsMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Étudiant", 42L);
        assertThat(ex.getMessage()).isEqualTo("Étudiant introuvable (id=42)");
    }

    @Test
    @DisplayName("UC-ERR-01b : ResourceNotFoundException(message) → message brut conservé")
    void resourceNotFoundException_withRawMessage_preservesMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Aucun paiement trouvé");
        assertThat(ex.getMessage()).isEqualTo("Aucun paiement trouvé");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-ERR-02 : BusinessException → message métier conservé
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-ERR-02 : BusinessException → message métier conservé")
    void businessException_preservesMessage() {
        BusinessException ex = new BusinessException("Email déjà utilisé");
        assertThat(ex.getMessage()).isEqualTo("Email déjà utilisé");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-ERR-03 : UnauthorizedException → message d'identifiants
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-ERR-03 : UnauthorizedException → message invalides conservé")
    void unauthorizedException_preservesMessage() {
        UnauthorizedException ex = new UnauthorizedException("Identifiants invalides");
        assertThat(ex.getMessage()).isEqualTo("Identifiants invalides");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-ERR-04 : ErrorBody — record contient tous les champs requis
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-ERR-04 : ErrorBody record → timestamp, status, code, message accessibles")
    void errorBody_record_accessorsWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        GlobalExceptionHandler.ErrorBody body =
                new GlobalExceptionHandler.ErrorBody(now, 404, "RESOURCE_NOT_FOUND",
                        "Étudiant introuvable (id=99)", null);

        assertThat(body.timestamp()).isEqualTo(now);
        assertThat(body.status()).isEqualTo(404);
        assertThat(body.code()).isEqualTo("RESOURCE_NOT_FOUND");
        assertThat(body.message()).isEqualTo("Étudiant introuvable (id=99)");
        assertThat(body.fields()).isNull();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-ERR-05 : ErrorBody — avec champs de validation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-ERR-05 : ErrorBody avec champs → fields non null et contient les erreurs")
    void errorBody_withValidationFields_containsFieldErrors() {
        java.util.Map<String, String> fields = java.util.Map.of(
                "email", "Format d'email invalide",
                "firstName", "Le prénom est obligatoire"
        );
        GlobalExceptionHandler.ErrorBody body =
                new GlobalExceptionHandler.ErrorBody(LocalDateTime.now(), 400,
                        "VALIDATION_ERROR", "Champs invalides", fields);

        assertThat(body.fields()).isNotNull();
        assertThat(body.fields()).containsKey("email");
        assertThat(body.fields()).containsKey("firstName");
        assertThat(body.fields().get("email")).isEqualTo("Format d'email invalide");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-ERR-06 : Hiérarchie — toutes les exceptions héritent RuntimeException
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-ERR-06 : ResourceNotFoundException, BusinessException, UnauthorizedException " +
                 "sont toutes des RuntimeException")
    void allCustomExceptions_extendRuntimeException() {
        assertThat(new ResourceNotFoundException("test"))
                .isInstanceOf(RuntimeException.class);
        assertThat(new BusinessException("test"))
                .isInstanceOf(RuntimeException.class);
        assertThat(new UnauthorizedException("test"))
                .isInstanceOf(RuntimeException.class);
    }
}
