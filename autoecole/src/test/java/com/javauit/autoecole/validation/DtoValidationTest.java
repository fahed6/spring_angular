package com.javauit.autoecole.validation;

import com.javauit.autoecole.dto.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cas d'utilisation — Validation des DTOs (contraintes @Valid)")
class DtoValidationTest {

    static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private <T> Set<String> fieldsWith(Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-01 : LoginRequest valide → aucune violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-01 : LoginRequest valide → aucune violation de contrainte")
    void loginRequest_validData_noViolations() {
        LoginRequest req = new LoginRequest("admin@autoecole.com", "admin123");
        assertThat(validator.validate(req)).isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-02 : LoginRequest — email vide → violation sur 'email'
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-02 : Email vide dans LoginRequest → violation 'email'")
    void loginRequest_blankEmail_violatesEmailConstraint() {
        LoginRequest req = new LoginRequest("", "admin123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("email");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-03 : LoginRequest — format email invalide → violation sur 'email'
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-03 : Email malformé 'pas_un_email' → violation format @Email")
    void loginRequest_invalidEmailFormat_violatesEmailConstraint() {
        LoginRequest req = new LoginRequest("pas_un_email", "admin123");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("email");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-04 : LoginRequest — mot de passe vide → violation sur 'password'
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-04 : Mot de passe vide dans LoginRequest → violation 'password'")
    void loginRequest_blankPassword_violatesNotBlank() {
        LoginRequest req = new LoginRequest("admin@autoecole.com", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("password");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-05 : StudentRequest valide → aucune violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-05 : StudentRequest complet et valide → aucune violation")
    void studentRequest_validData_noViolations() {
        StudentRequest req = new StudentRequest("Mohamed", "Benali",
                "m.benali@test.com", "0555123456", null, "Alger", "ACTIVE");
        assertThat(validator.validate(req)).isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-06 : StudentRequest — prénom et nom vides → 2 violations
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-06 : StudentRequest sans prénom ni nom → violations 'firstName' et 'lastName'")
    void studentRequest_blankNames_twoViolations() {
        StudentRequest req = new StudentRequest("", "", null, null, null, null, "ACTIVE");
        Set<ConstraintViolation<StudentRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).containsExactlyInAnyOrder("firstName", "lastName");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-07 : PlanningRequest valide → aucune violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-07 : PlanningRequest valide (studentId + scheduledAt + type) → aucune violation")
    void planningRequest_validData_noViolations() {
        PlanningRequest req = new PlanningRequest(1L, null,
                LocalDateTime.now().plusDays(1), "CODE", "SCHEDULED", null);
        assertThat(validator.validate(req)).isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-08 : PlanningRequest — champs obligatoires null → violations
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-08 : PlanningRequest sans studentId, scheduledAt ni type → 3 violations")
    void planningRequest_missingRequiredFields_multipleViolations() {
        PlanningRequest req = new PlanningRequest(null, null, null, null, null, null);
        Set<ConstraintViolation<PlanningRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("studentId", "scheduledAt", "type");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-09 : PaymentRequest valide → aucune violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-09 : PaymentRequest valide (studentId + amount > 0) → aucune violation")
    void paymentRequest_validData_noViolations() {
        PaymentRequest req = new PaymentRequest(1L, new BigDecimal("15000"),
                null, "CASH", "PAID", null);
        assertThat(validator.validate(req)).isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-10 : PaymentRequest — montant nul ou zéro → violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-10 : Montant zéro dans PaymentRequest → violation 'amount'")
    void paymentRequest_zeroAmount_violatesDecimalMin() {
        PaymentRequest req = new PaymentRequest(1L, BigDecimal.ZERO,
                null, "CASH", "PAID", null);
        Set<ConstraintViolation<PaymentRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("amount");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-11 : ProgressRequest — score hors plage [0-100] → violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-11 : Score 150 dans ProgressRequest → violation 'score' @Max(100)")
    void progressRequest_scoreAbove100_violatesMax() {
        ProgressRequest req = new ProgressRequest(1L, "CODE",
                new BigDecimal("10"), new BigDecimal("20"), 150);
        Set<ConstraintViolation<ProgressRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("score");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-VAL-12 : ProgressRequest — score négatif → violation
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-VAL-12 : Score -5 dans ProgressRequest → violation 'score' @Min(0)")
    void progressRequest_negativeScore_violatesMin() {
        ProgressRequest req = new ProgressRequest(1L, "CONDUITE",
                new BigDecimal("5"), new BigDecimal("20"), -5);
        Set<ConstraintViolation<ProgressRequest>> violations = validator.validate(req);
        assertThat(fieldsWith(violations)).contains("score");
    }
}
