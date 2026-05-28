package com.javauit.autoecole.dto;

import java.util.List;

public record DashboardResponse(
        long totalStudents,
        long activeStudents,
        long totalUsers,
        long newStudentsThisMonth,
        // student status breakdown
        long completedStudents,
        long suspendedStudents,
        // monthly enrollments (last 6 months)
        List<String> enrollmentLabels,
        List<Long>   enrollmentData,
        // payments by method
        long cashPayments,
        long cardPayments,
        long transferPayments,
        // planning by status
        long scheduledSessions,
        long completedSessions,
        long cancelledSessions
) {}
