package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.LoginRequest;
import com.javauit.autoecole.dto.LoginResponse;
import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.exception.UnauthorizedException;
import com.javauit.autoecole.repository.UserRepository;
import com.javauit.autoecole.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Cas d'utilisation — Service d'authentification")
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock JwtUtil        jwtUtil;

    // Real encoder — we test actual BCrypt matching logic
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, jwtUtil, passwordEncoder);
    }

    private User buildUser(String email, String rawPassword, User.Role role, boolean active) {
        User u = new User();
        u.setEmail(email);
        u.setFullName("Test User");
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        u.setIsActive(active);
        return u;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-AUTH-01 : Connexion admin réussie
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-AUTH-01 : Connexion admin valide → token JWT retourné")
    void login_validAdminCredentials_returnsTokenAndRole() {
        User admin = buildUser("admin@autoecole.com", "admin123", User.Role.ADMIN, true);
        when(userRepository.findByEmail("admin@autoecole.com")).thenReturn(Optional.of(admin));
        when(jwtUtil.generateToken("admin@autoecole.com", "ADMIN")).thenReturn("mock.jwt.token");

        LoginResponse response = authService.login(new LoginRequest("admin@autoecole.com", "admin123"));

        assertThat(response.token()).isEqualTo("mock.jwt.token");
        assertThat(response.role()).isEqualTo("ADMIN");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-AUTH-02 : Connexion staff réussie
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-AUTH-02 : Connexion staff valide → token avec rôle STAFF")
    void login_validStaffCredentials_returnsStaffRole() {
        User staff = buildUser("staff@autoecole.com", "staff123", User.Role.STAFF, true);
        when(userRepository.findByEmail("staff@autoecole.com")).thenReturn(Optional.of(staff));
        when(jwtUtil.generateToken("staff@autoecole.com", "STAFF")).thenReturn("staff.jwt.token");

        LoginResponse response = authService.login(new LoginRequest("staff@autoecole.com", "staff123"));

        assertThat(response.role()).isEqualTo("STAFF");
        assertThat(response.token()).isEqualTo("staff.jwt.token");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-AUTH-03 : Email introuvable → 401
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-AUTH-03 : Email inexistant → UnauthorizedException 'Identifiants invalides'")
    void login_unknownEmail_throwsUnauthorized() {
        when(userRepository.findByEmail("inconnu@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("inconnu@example.com", "n'importe")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Identifiants invalides");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-AUTH-04 : Mot de passe incorrect → 401
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-AUTH-04 : Mauvais mot de passe → UnauthorizedException")
    void login_wrongPassword_throwsUnauthorized() {
        User admin = buildUser("admin@autoecole.com", "admin123", User.Role.ADMIN, true);
        when(userRepository.findByEmail("admin@autoecole.com")).thenReturn(Optional.of(admin));

        assertThatThrownBy(() -> authService.login(new LoginRequest("admin@autoecole.com", "mauvais_mdp")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Identifiants invalides");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-AUTH-05 : Compte désactivé → 401 avec message spécifique
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-AUTH-05 : Compte désactivé → UnauthorizedException message désactivation")
    void login_disabledAccount_throwsUnauthorizedWithSpecificMessage() {
        User disabled = buildUser("staff@autoecole.com", "staff123", User.Role.STAFF, false);
        when(userRepository.findByEmail("staff@autoecole.com")).thenReturn(Optional.of(disabled));

        assertThatThrownBy(() -> authService.login(new LoginRequest("staff@autoecole.com", "staff123")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("désactivé");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UC-AUTH-06 : Token non généré si compte désactivé (jwtUtil jamais appelé)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    @DisplayName("UC-AUTH-06 : Compte désactivé → generateToken jamais appelé")
    void login_disabledAccount_neverCallsJwtUtil() {
        User disabled = buildUser("staff@autoecole.com", "staff123", User.Role.STAFF, false);
        when(userRepository.findByEmail("staff@autoecole.com")).thenReturn(Optional.of(disabled));

        assertThatThrownBy(() -> authService.login(new LoginRequest("staff@autoecole.com", "staff123")))
                .isInstanceOf(UnauthorizedException.class);

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
