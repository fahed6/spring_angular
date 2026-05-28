package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.LoginRequest;
import com.javauit.autoecole.dto.LoginResponse;
import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.exception.UnauthorizedException;
import com.javauit.autoecole.repository.UserRepository;
import com.javauit.autoecole.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.jwtUtil         = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("Identifiants invalides"));

        if (!user.getIsActive())
            throw new UnauthorizedException("Ce compte est désactivé. Contactez l'administrateur.");

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash()))
            throw new UnauthorizedException("Identifiants invalides");

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getRole().name(), user.getFullName());
    }
}
