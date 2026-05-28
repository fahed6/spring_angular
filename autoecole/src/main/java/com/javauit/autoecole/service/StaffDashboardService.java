package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.StaffDashboardResponse;
import com.javauit.autoecole.repository.PaymentRepository;
import com.javauit.autoecole.repository.PlanningRepository;
import com.javauit.autoecole.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StaffDashboardService {

    private final StudentRepository  studentRepository;
    private final PlanningRepository planningRepository;
    private final PaymentRepository  paymentRepository;

    public StaffDashboardService(StudentRepository studentRepository,
                                 PlanningRepository planningRepository,
                                 PaymentRepository paymentRepository) {
        this.studentRepository  = studentRepository;
        this.planningRepository = planningRepository;
        this.paymentRepository  = paymentRepository;
    }

    public StaffDashboardResponse getStats() {
        return new StaffDashboardResponse(
                studentRepository.count(),
                studentRepository.countByStatus("ACTIVE"),
                planningRepository.countByStatus("SCHEDULED"),
                planningRepository.countByStatus("COMPLETED"),
                planningRepository.countByStatus("CANCELLED"),
                planningRepository.countByType("CODE"),
                planningRepository.countByType("CONDUITE"),
                paymentRepository.countByStatus("PAID"),
                paymentRepository.countByStatus("PENDING"),
                paymentRepository.countByStatus("PARTIAL")
        );
    }
}
