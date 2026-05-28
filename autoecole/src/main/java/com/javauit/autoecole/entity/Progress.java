package com.javauit.autoecole.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(length = 10, nullable = false)
    private String category; // CODE | CONDUITE

    @Column(name = "hours_completed", precision = 5, scale = 1)
    private BigDecimal hoursCompleted = BigDecimal.ZERO;

    @Column(name = "hours_required", precision = 5, scale = 1)
    private BigDecimal hoursRequired = new BigDecimal("20.0");

    private Integer score;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getHoursCompleted() { return hoursCompleted; }
    public void setHoursCompleted(BigDecimal hoursCompleted) { this.hoursCompleted = hoursCompleted; }
    public BigDecimal getHoursRequired() { return hoursRequired; }
    public void setHoursRequired(BigDecimal hoursRequired) { this.hoursRequired = hoursRequired; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
}