package com.javauit.autoecole.repository;

import com.javauit.autoecole.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByStudentId(Long studentId);
    Optional<Progress> findByStudentIdAndCategory(Long studentId, String category);
}