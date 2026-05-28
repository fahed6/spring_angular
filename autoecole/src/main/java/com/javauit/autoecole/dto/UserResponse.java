package com.javauit.autoecole.dto;

import com.javauit.autoecole.entity.User;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String role,
        Boolean isActive,
        LocalDateTime createdAt) {

    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getRole().name(),
                u.getIsActive(),
                u.getCreatedAt());
    }
}
