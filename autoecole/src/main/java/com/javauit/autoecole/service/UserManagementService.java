package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.UserRequest;
import com.javauit.autoecole.dto.UserResponse;
import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse getById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    public UserResponse create(UserRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent())
            throw new RuntimeException("Email already in use");

        User u = new User();
        u.setFullName(req.fullName());
        u.setEmail(req.email());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setRole(User.Role.valueOf(req.role().toUpperCase()));
        if (req.isActive() != null) u.setIsActive(req.isActive());
        return UserResponse.from(userRepository.save(u));
    }

    public UserResponse update(Long id, UserRequest req) {
        User u = findOrThrow(id);
        if (req.fullName()  != null) u.setFullName(req.fullName());
        if (req.email()     != null) u.setEmail(req.email());
        if (req.password()  != null && !req.password().isBlank())
            u.setPasswordHash(passwordEncoder.encode(req.password()));
        if (req.role()      != null) u.setRole(User.Role.valueOf(req.role().toUpperCase()));
        if (req.isActive()  != null) u.setIsActive(req.isActive());
        return UserResponse.from(userRepository.save(u));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) throw new RuntimeException("User not found");
        userRepository.deleteById(id);
    }

    public UserResponse toggleActive(Long id) {
        User u = findOrThrow(id);
        u.setIsActive(!u.getIsActive());
        return UserResponse.from(userRepository.save(u));
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
