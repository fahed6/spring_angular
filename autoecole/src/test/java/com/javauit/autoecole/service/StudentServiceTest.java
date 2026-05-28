package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.StudentRequest;
import com.javauit.autoecole.dto.StudentResponse;
import com.javauit.autoecole.entity.Student;
import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cas d'utilisation — Gestion des étudiants")
class StudentServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock UserRepository    userRepository;
    @InjectMocks StudentService studentService;

    private Student buildStudent(Long id, String firstName, String lastName, String status) {
        Student s = new Student();
        // Reflect-set id for testing
        try {
            var f = Student.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(s, id);
        } catch (Exception ignored) {}
        s.setFirstName(firstName);
        s.setLastName(lastName);
        s.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + "@test.com");
        s.setStatus(status);
        return s;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-01 : Lister tous les étudiants (sans filtre)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-01 : Lister étudiants sans filtre → liste complète")
    void getAll_noSearch_returnsAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(
                buildStudent(1L, "Mohamed", "Benali", "ACTIVE"),
                buildStudent(2L, "Sara", "Mokrani", "COMPLETED")
        ));

        List<StudentResponse> result = studentService.getAll(null);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("Mohamed");
        assertThat(result.get(1).status()).isEqualTo("COMPLETED");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-02 : Rechercher des étudiants par nom
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-02 : Recherche 'Benali' → méthode search() appelée")
    void getAll_withSearch_callsSearchRepository() {
        when(studentRepository.search("Benali")).thenReturn(List.of(
                buildStudent(1L, "Mohamed", "Benali", "ACTIVE")
        ));

        List<StudentResponse> result = studentService.getAll("Benali");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).lastName()).isEqualTo("Benali");
        verify(studentRepository).search("Benali");
        verify(studentRepository, never()).findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-03 : Obtenir un étudiant par ID existant
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-03 : Obtenir étudiant id=1 → données correctes retournées")
    void getById_existingId_returnsStudent() {
        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(buildStudent(1L, "Mohamed", "Benali", "ACTIVE")));

        StudentResponse response = studentService.getById(1L);

        assertThat(response.firstName()).isEqualTo("Mohamed");
        assertThat(response.status()).isEqualTo("ACTIVE");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-04 : Obtenir un étudiant avec ID inexistant → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-04 : Étudiant id=999 introuvable → RuntimeException 'not found'")
    void getById_missingId_throwsException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-05 : Créer un étudiant avec données valides
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-05 : Créer étudiant valide → enregistrement en base")
    void create_validRequest_savesStudent() {
        Student saved = buildStudent(1L, "Karim", "Amrani", "ACTIVE");
        when(studentRepository.save(any(Student.class))).thenReturn(saved);
        when(userRepository.findByEmail("admin@autoecole.com")).thenReturn(Optional.empty());

        StudentRequest req = new StudentRequest("Karim", "Amrani",
                "k.amrani@test.com", "0555000111",
                LocalDate.of(2001, 3, 20), "Oran", "ACTIVE");

        StudentResponse response = studentService.create(req, "admin@autoecole.com");

        assertThat(response.firstName()).isEqualTo("Karim");
        verify(studentRepository).save(any(Student.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-06 : Modifier un étudiant existant
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-06 : Modifier étudiant id=1 → champs mis à jour")
    void update_existingStudent_updatesFields() {
        Student existing = buildStudent(1L, "Mohamed", "Benali", "ACTIVE");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        StudentRequest req = new StudentRequest("Mohamed", "Benali",
                null, null, null, "Constantine", "SUSPENDED");

        StudentResponse response = studentService.update(1L, req);

        assertThat(response.address()).isEqualTo("Constantine");
        assertThat(response.status()).isEqualTo("SUSPENDED");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-07 : Modifier un étudiant inexistant → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-07 : Modifier étudiant id=999 introuvable → exception")
    void update_missingStudent_throwsException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        StudentRequest req = new StudentRequest("X", "Y", null, null, null, null, null);
        assertThatThrownBy(() -> studentService.update(999L, req))
                .isInstanceOf(RuntimeException.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-08 : Supprimer un étudiant existant
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-08 : Supprimer étudiant id=1 → deleteById appelé")
    void delete_existingStudent_callsRepository() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        studentService.delete(1L);

        verify(studentRepository).deleteById(1L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-STU-09 : Supprimer un étudiant inexistant → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-STU-09 : Supprimer étudiant id=999 introuvable → exception")
    void delete_missingStudent_throwsException() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(studentRepository, never()).deleteById(any());
    }
}
