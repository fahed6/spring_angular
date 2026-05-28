package com.javauit.autoecole.dto;

import com.javauit.autoecole.entity.Student;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        LocalDate dateOfBirth,
        String address,
        String status,
        LocalDateTime registeredAt,
        String registeredBy) {

    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getPhone(),
                s.getDateOfBirth(),
                s.getAddress(),
                s.getStatus(),
                s.getRegisteredAt(),
                s.getRegisteredBy() != null ? s.getRegisteredBy().getFullName() : null);
    }
}
