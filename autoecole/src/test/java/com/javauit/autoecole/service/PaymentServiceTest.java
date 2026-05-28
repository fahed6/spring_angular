package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.PaymentRequest;
import com.javauit.autoecole.dto.PaymentResponse;
import com.javauit.autoecole.entity.Payment;
import com.javauit.autoecole.entity.Student;
import com.javauit.autoecole.repository.PaymentRepository;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cas d'utilisation — Gestion des paiements")
class PaymentServiceTest {

    @Mock PaymentRepository paymentRepository;
    @Mock StudentRepository studentRepository;
    @Mock UserRepository    userRepository;
    @InjectMocks PaymentService paymentService;

    private Student buildStudent(Long id) {
        Student s = new Student();
        s.setFirstName("Test"); s.setLastName("Student"); s.setStatus("ACTIVE");
        try {
            var f = Student.class.getDeclaredField("id");
            f.setAccessible(true); f.set(s, id);
        } catch (Exception ignored) {}
        return s;
    }

    private Payment buildPayment(Long id, BigDecimal amount, String method, String status) {
        Payment p = new Payment();
        p.setStudent(buildStudent(1L));
        p.setAmount(amount);
        p.setMethod(method);
        p.setStatus(status);
        p.setPaidAt(LocalDate.now());
        try {
            var f = Payment.class.getDeclaredField("id");
            f.setAccessible(true); f.set(p, id);
        } catch (Exception ignored) {}
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PAY-01 : Lister tous les paiements
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PAY-01 : Lister tous les paiements → liste ordonnée par date")
    void getAll_noFilter_returnsAllPayments() {
        when(paymentRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(
                buildPayment(1L, new BigDecimal("15000"), "CASH", "PAID"),
                buildPayment(2L, new BigDecimal("8000"),  "CARD", "PENDING")
        ));

        List<PaymentResponse> result = paymentService.getAll(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo("PAID");
        assertThat(result.get(1).method()).isEqualTo("CARD");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PAY-02 : Enregistrer un paiement CASH complet (PAID)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PAY-02 : Enregistrer paiement CASH 15000 DZD → statut PAID")
    void create_cashPayment_savedCorrectly() {
        Student student = buildStudent(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(userRepository.findByEmail("staff@autoecole.com")).thenReturn(Optional.empty());
        Payment saved = buildPayment(1L, new BigDecimal("15000"), "CASH", "PAID");
        when(paymentRepository.save(any())).thenReturn(saved);

        PaymentRequest req = new PaymentRequest(1L, new BigDecimal("15000"),
                LocalDate.now(), "CASH", "PAID", "Règlement formation code");

        PaymentResponse response = paymentService.create(req, "staff@autoecole.com");

        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("15000"));
        assertThat(response.method()).isEqualTo("CASH");
        assertThat(response.status()).isEqualTo("PAID");
        verify(paymentRepository).save(any(Payment.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PAY-03 : Enregistrer un paiement partiel (PARTIAL)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PAY-03 : Paiement partiel TRANSFER → statut PARTIAL")
    void create_partialTransferPayment_statusIsPartial() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(buildStudent(1L)));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        Payment saved = buildPayment(2L, new BigDecimal("5000"), "TRANSFER", "PARTIAL");
        when(paymentRepository.save(any())).thenReturn(saved);

        PaymentRequest req = new PaymentRequest(1L, new BigDecimal("5000"),
                LocalDate.now(), "TRANSFER", "PARTIAL", "Premier versement");

        PaymentResponse response = paymentService.create(req, "staff@autoecole.com");

        assertThat(response.status()).isEqualTo("PARTIAL");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PAY-04 : Paiement pour étudiant inexistant → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PAY-04 : Paiement pour studentId=999 introuvable → exception")
    void create_unknownStudent_throwsException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        PaymentRequest req = new PaymentRequest(999L, new BigDecimal("10000"),
                LocalDate.now(), "CASH", "PAID", null);

        assertThatThrownBy(() -> paymentService.create(req, "staff@autoecole.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(paymentRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PAY-05 : Modifier le statut d'un paiement en attente → PAID
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PAY-05 : Mettre à jour paiement PENDING → PAID après règlement")
    void update_pendingToPaid_updatesStatus() {
        Payment existing = buildPayment(1L, new BigDecimal("8000"), "CARD", "PENDING");
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentRequest req = new PaymentRequest(null, null, null, null, "PAID", "Paiement reçu");

        PaymentResponse response = paymentService.update(1L, req);

        assertThat(response.status()).isEqualTo("PAID");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PAY-06 : Supprimer un paiement inexistant → exception, aucune suppression
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PAY-06 : Supprimer paiement id=999 introuvable → exception sans deleteById")
    void delete_missingPayment_throwsWithoutCallingDelete() {
        when(paymentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> paymentService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(paymentRepository, never()).deleteById(any());
    }
}
