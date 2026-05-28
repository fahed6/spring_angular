package com.javauit.autoecole.dto;

import com.javauit.autoecole.entity.Progress;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProgressResponse(
        Long id,
        Long studentId,
        String studentName,
        String category,
        BigDecimal hoursCompleted,
        BigDecimal hoursRequired,
        Integer score,
        LocalDateTime updatedAt,
        String updatedBy) {

    public static ProgressResponse from(Progress p) {
        return new ProgressResponse(
                p.getId(),
                p.getStudent().getId(),
                p.getStudent().getFirstName() + " " + p.getStudent().getLastName(),
                p.getCategory(),
                p.getHoursCompleted(),
                p.getHoursRequired(),
                p.getScore(),
                p.getUpdatedAt(),
                p.getUpdatedBy() != null ? p.getUpdatedBy().getFullName() : null);
    }
}
