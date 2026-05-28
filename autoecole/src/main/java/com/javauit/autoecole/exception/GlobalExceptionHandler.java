package com.javauit.autoecole.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── 404 Not Found ─────────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorBody> handleNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage());
    }

    // ── 400 Bad Request (business logic) ─────────────────────────────────────
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorBody> handleBusiness(BusinessException ex) {
        return build(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", ex.getMessage());
    }

    // ── 400 Bean Validation failures (@Valid) ─────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.put(fe.getField(), fe.getDefaultMessage());
        }
        ErrorBody body = new ErrorBody(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Un ou plusieurs champs sont invalides",
                fields);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ── 400 Path variable type mismatch ──────────────────────────────────────
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorBody> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Paramètre '" + ex.getName() + "' invalide : valeur reçue = '" + ex.getValue() + "'";
        return build(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", message);
    }

    // ── 401 Unauthorized ──────────────────────────────────────────────────────
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorBody> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage());
    }

    // ── 500 Fallback ──────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> handleGeneral(Exception ex) {
        // Rethrow known app exceptions that slipped through
        if (ex instanceof ResourceNotFoundException rne) return handleNotFound(rne);
        if (ex instanceof BusinessException be) return handleBusiness(be);

        // Legacy RuntimeException with keyword detection
        if (ex instanceof RuntimeException) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Erreur interne";
            if (msg.contains("Invalid credentials") || msg.contains("Account is disabled"))
                return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Identifiants invalides ou compte désactivé");
            if (msg.contains("introuvable") || msg.contains("not found"))
                return build(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", msg);
            return build(HttpStatus.BAD_REQUEST, "BUSINESS_ERROR", msg);
        }

        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Une erreur inattendue s'est produite. Veuillez contacter l'administrateur.");
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private ResponseEntity<ErrorBody> build(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(new ErrorBody(LocalDateTime.now(), status.value(), code, message, null));
    }

    // ── Error response structure ──────────────────────────────────────────────
    public record ErrorBody(
            LocalDateTime timestamp,
            int status,
            String code,
            String message,
            Map<String, String> fields) {}
}
