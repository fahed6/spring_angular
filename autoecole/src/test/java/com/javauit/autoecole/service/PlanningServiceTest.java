package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.PlanningRequest;
import com.javauit.autoecole.dto.PlanningResponse;
import com.javauit.autoecole.entity.Planning;
import com.javauit.autoecole.entity.Student;
import com.javauit.autoecole.repository.PlanningRepository;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cas d'utilisation — Planning des séances")
class PlanningServiceTest {

    @Mock PlanningRepository planningRepository;
    @Mock StudentRepository  studentRepository;
    @Mock UserRepository     userRepository;
    @InjectMocks PlanningService planningService;

    private Student buildStudent(Long id) {
        Student s = new Student();
        s.setFirstName("Test");
        s.setLastName("Student");
        s.setStatus("ACTIVE");
        try {
            var f = Student.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(s, id);
        } catch (Exception ignored) {}
        return s;
    }

    private Planning buildPlanning(Long id, String type, String status) {
        Planning p = new Planning();
        p.setStudent(buildStudent(1L));
        p.setType(type);
        p.setStatus(status);
        p.setScheduledAt(LocalDateTime.now().plusDays(1));
        try {
            var f = Planning.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(p, id);
        } catch (Exception ignored) {}
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-01 : Lister toutes les séances (sans filtre étudiant)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-01 : Lister toutes les séances → liste ordonnée par date")
    void getAll_noFilter_returnsAllSessions() {
        when(planningRepository.findAllByOrderByScheduledAtDesc()).thenReturn(List.of(
                buildPlanning(1L, "CODE", "SCHEDULED"),
                buildPlanning(2L, "CONDUITE", "COMPLETED")
        ));

        List<PlanningResponse> result = planningService.getAll(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo("CODE");
        assertThat(result.get(1).status()).isEqualTo("COMPLETED");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-02 : Lister les séances d'un étudiant spécifique
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-02 : Filtre par studentId=1 → séances de cet étudiant uniquement")
    void getAll_withStudentFilter_returnsStudentSessions() {
        when(planningRepository.findByStudentIdOrderByScheduledAtDesc(1L))
                .thenReturn(List.of(buildPlanning(1L, "CONDUITE", "SCHEDULED")));

        List<PlanningResponse> result = planningService.getAll(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).type()).isEqualTo("CONDUITE");
        verify(planningRepository).findByStudentIdOrderByScheduledAtDesc(1L);
        verify(planningRepository, never()).findAllByOrderByScheduledAtDesc();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-03 : Planifier une nouvelle séance CODE
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-03 : Créer séance CODE pour étudiant id=1 → enregistrement réussi")
    void create_codeSession_savedWithCorrectType() {
        Student student = buildStudent(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Planning saved = buildPlanning(1L, "CODE", "SCHEDULED");
        when(planningRepository.save(any())).thenReturn(saved);

        PlanningRequest req = new PlanningRequest(1L, null,
                LocalDateTime.now().plusDays(1), "CODE", "SCHEDULED", "Révision signalisation");

        PlanningResponse response = planningService.create(req);

        assertThat(response.type()).isEqualTo("CODE");
        assertThat(response.status()).isEqualTo("SCHEDULED");
        verify(planningRepository).save(any(Planning.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-04 : Planifier une séance pour un étudiant inexistant → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-04 : Créer séance pour studentId=999 inexistant → exception")
    void create_unknownStudent_throwsException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        PlanningRequest req = new PlanningRequest(999L, null,
                LocalDateTime.now().plusDays(1), "CONDUITE", "SCHEDULED", null);

        assertThatThrownBy(() -> planningService.create(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(planningRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-05 : Marquer une séance comme COMPLETED
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-05 : Mettre à jour statut → SCHEDULED → COMPLETED")
    void update_changeStatusToCompleted_persistsChange() {
        Planning existing = buildPlanning(1L, "CONDUITE", "SCHEDULED");
        when(planningRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(planningRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PlanningRequest req = new PlanningRequest(null, null, null, null, "COMPLETED", "Séance terminée");

        PlanningResponse response = planningService.update(1L, req);

        assertThat(response.status()).isEqualTo("COMPLETED");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-06 : Annuler une séance inexistante → 404
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-06 : Modifier séance id=999 introuvable → exception")
    void update_missingSesssion_throwsException() {
        when(planningRepository.findById(999L)).thenReturn(Optional.empty());

        PlanningRequest req = new PlanningRequest(null, null, null, null, "CANCELLED", null);

        assertThatThrownBy(() -> planningService.update(999L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-PLN-07 : Supprimer une séance
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-PLN-07 : Supprimer séance id=1 → deleteById appelé")
    void delete_existingSession_callsRepository() {
        when(planningRepository.existsById(1L)).thenReturn(true);

        planningService.delete(1L);

        verify(planningRepository).deleteById(1L);
    }
}
