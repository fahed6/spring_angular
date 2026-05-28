package com.javauit.autoecole.repository;

import com.javauit.autoecole.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.lastName)  LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.email)     LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Student> search(@Param("q") String query);

    long countByStatus(String status);

    long countByRegisteredAtAfter(LocalDateTime date);

    long countByRegisteredAtBetween(LocalDateTime from, LocalDateTime to);
}