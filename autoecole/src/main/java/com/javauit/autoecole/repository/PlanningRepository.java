package com.javauit.autoecole.repository;

import com.javauit.autoecole.entity.Planning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanningRepository extends JpaRepository<Planning, Long> {
    List<Planning> findByStudentIdOrderByScheduledAtDesc(Long studentId);
    List<Planning> findAllByOrderByScheduledAtDesc();

    long countByStatus(String status);
    long countByType(String type);
}