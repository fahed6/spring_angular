package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.DashboardResponse;
import com.javauit.autoecole.repository.PaymentRepository;
import com.javauit.autoecole.repository.PlanningRepository;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    private final StudentRepository studentRepository;
    private final UserRepository    userRepository;
    private final PaymentRepository paymentRepository;
    private final PlanningRepository planningRepository;

    public DashboardService(StudentRepository studentRepository,
                            UserRepository userRepository,
                            PaymentRepository paymentRepository,
                            PlanningRepository planningRepository) {
        this.studentRepository  = studentRepository;
        this.userRepository     = userRepository;
        this.paymentRepository  = paymentRepository;
        this.planningRepository = planningRepository;
    }

    public DashboardResponse getStats() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        long total     = studentRepository.count();
        long active    = studentRepository.countByStatus("ACTIVE");
        long completed = studentRepository.countByStatus("COMPLETED");
        long suspended = studentRepository.countByStatus("SUSPENDED");
        long users     = userRepository.count();
        long newMonth  = studentRepository.countByRegisteredAtAfter(startOfMonth);

        // Monthly enrollments — last 6 months
        List<String> labels = new ArrayList<>();
        List<Long>   data   = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym   = YearMonth.now().minusMonths(i);
            LocalDateTime from = ym.atDay(1).atStartOfDay();
            LocalDateTime to   = ym.atEndOfMonth().atTime(23, 59, 59);
            String label = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH)
                           + " " + ym.getYear();
            labels.add(label);
            data.add(studentRepository.countByRegisteredAtBetween(from, to));
        }

        return new DashboardResponse(
                total, active, users, newMonth,
                completed, suspended,
                labels, data,
                paymentRepository.countByMethod("CASH"),
                paymentRepository.countByMethod("CARD"),
                paymentRepository.countByMethod("TRANSFER"),
                planningRepository.countByStatus("SCHEDULED"),
                planningRepository.countByStatus("COMPLETED"),
                planningRepository.countByStatus("CANCELLED")
        );
    }
}
