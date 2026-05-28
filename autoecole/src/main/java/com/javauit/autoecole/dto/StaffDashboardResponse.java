package com.javauit.autoecole.dto;

public record StaffDashboardResponse(
        long totalStudents,
        long activeStudents,
        // planning by status
        long scheduledSessions,
        long completedSessions,
        long cancelledSessions,
        // planning by type
        long codeSessions,
        long conduiteSessions,
        // payments by status
        long paidPayments,
        long pendingPayments,
        long partialPayments
) {}
