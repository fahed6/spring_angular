package com.javauit.autoecole.dto;

import com.javauit.autoecole.entity.Planning;
import java.time.LocalDateTime;

public record PlanningResponse(
        Long id,
        Long studentId,
        String studentName,
        Long instructorId,
        String instructorName,
        LocalDateTime scheduledAt,
        String type,
        String status,
        String notes,
        LocalDateTime createdAt) {

    public static PlanningResponse from(Planning p) {
        return new PlanningResponse(
                p.getId(),
                p.getStudent().getId(),
                p.getStudent().getFirstName() + " " + p.getStudent().getLastName(),
                p.getInstructor() != null ? p.getInstructor().getId() : null,
                p.getInstructor() != null ? p.getInstructor().getFullName() : null,
                p.getScheduledAt(),
                p.getType(),
                p.getStatus(),
                p.getNotes(),
                p.getCreatedAt());
    }
}
