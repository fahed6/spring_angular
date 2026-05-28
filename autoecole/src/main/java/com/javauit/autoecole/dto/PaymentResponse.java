package com.javauit.autoecole.dto;

import com.javauit.autoecole.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long studentId,
        String studentName,
        BigDecimal amount,
        LocalDate paidAt,
        String method,
        String status,
        String description,
        String recordedBy,
        LocalDateTime createdAt) {

    public static PaymentResponse from(Payment p) {
        return new PaymentResponse(
                p.getId(),
                p.getStudent().getId(),
                p.getStudent().getFirstName() + " " + p.getStudent().getLastName(),
                p.getAmount(),
                p.getPaidAt(),
                p.getMethod(),
                p.getStatus(),
                p.getDescription(),
                p.getRecordedBy() != null ? p.getRecordedBy().getFullName() : null,
                p.getCreatedAt());
    }
}
