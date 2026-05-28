package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.UserRequest;
import com.javauit.autoecole.dto.UserResponse;
import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cas d'utilisation — Gestion des utilisateurs système")
class UserManagementServiceTest {

    @Mock UserRepository userRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    UserManagementService userManagementService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        userManagementService = new UserManagementService(userRepository, passwordEncoder);
    }

    private User buildUser(Long id, String email, User.Role role, boolean active) {
        User u = new User();
        u.setFullName("Test User");
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode("pass123"));
        u.setRole(role);
        u.setIsActive(active);
        try {
            var f = User.class.getDeclaredField("id");
            f.setAccessible(true);
            f.set(u, id);
        } catch (Exception ignored) {}
        return u;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-01 : Lister tous les utilisateurs
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-01 : Lister utilisateurs → liste complète retournée")
    void getAll_returnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(
                buildUser(1L, "admin@autoecole.com", User.Role.ADMIN, true),
                buildUser(2L, "staff@autoecole.com", User.Role.STAFF, true)
        ));

        List<UserResponse> result = userManagementService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).role()).isEqualTo("ADMIN");
        assertThat(result.get(1).role()).isEqualTo("STAFF");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-02 : Créer un compte avec email unique → succès
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-02 : Créer compte staff email unique → enregistrement réussi")
    void create_uniqueEmail_savesUser() {
        when(userRepository.findByEmail("nouveau@autoecole.com")).thenReturn(Optional.empty());
        User saved = buildUser(3L, "nouveau@autoecole.com", User.Role.STAFF, true);
        when(userRepository.save(any())).thenReturn(saved);

        UserRequest req = new UserRequest("Nouveau Staff", "nouveau@autoecole.com", "motdepasse", "STAFF", true);

        UserResponse response = userManagementService.create(req);

        assertThat(response.email()).isEqualTo("nouveau@autoecole.com");
        assertThat(response.role()).isEqualTo("STAFF");
        verify(userRepository).save(any(User.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-03 : Créer un compte avec email déjà utilisé → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-03 : Email déjà existant → RuntimeException 'already in use'")
    void create_duplicateEmail_throwsException() {
        when(userRepository.findByEmail("admin@autoecole.com"))
                .thenReturn(Optional.of(buildUser(1L, "admin@autoecole.com", User.Role.ADMIN, true)));

        UserRequest req = new UserRequest("Doublon", "admin@autoecole.com", "pass", "STAFF", true);

        assertThatThrownBy(() -> userManagementService.create(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already in use");

        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-04 : Mot de passe hashé — jamais stocké en clair
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-04 : Création → mot de passe hashé BCrypt (jamais en clair)")
    void create_passwordIsHashed_notStoredInPlainText() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            // Le hash BCrypt commence toujours par $2a$, $2b$ ou $2y$
            assertThat(saved.getPasswordHash()).startsWith("$2");
            assertThat(saved.getPasswordHash()).isNotEqualTo("motdepasse123");
            return saved;
        });

        UserRequest req = new UserRequest("Test", "t@t.com", "motdepasse123", "STAFF", true);
        userManagementService.create(req);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-05 : Basculer l'état actif d'un compte
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-05 : toggleActive sur compte actif → désactivé (false)")
    void toggleActive_activeUser_becomesInactive() {
        User active = buildUser(2L, "staff@autoecole.com", User.Role.STAFF, true);
        when(userRepository.findById(2L)).thenReturn(Optional.of(active));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userManagementService.toggleActive(2L);

        assertThat(response.isActive()).isFalse();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-06 : Basculer l'état inactif → actif
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-06 : toggleActive sur compte inactif → réactivé (true)")
    void toggleActive_inactiveUser_becomesActive() {
        User inactive = buildUser(2L, "staff@autoecole.com", User.Role.STAFF, false);
        when(userRepository.findById(2L)).thenReturn(Optional.of(inactive));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserResponse response = userManagementService.toggleActive(2L);

        assertThat(response.isActive()).isTrue();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-USR-07 : Supprimer un utilisateur inexistant → exception
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-USR-07 : Supprimer utilisateur id=999 introuvable → exception")
    void delete_missingUser_throwsException() {
        when(userRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> userManagementService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(userRepository, never()).deleteById(any());
    }
}
