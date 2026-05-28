package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.ProgressRequest;
import com.javauit.autoecole.dto.ProgressResponse;
import com.javauit.autoecole.entity.Progress;
import com.javauit.autoecole.repository.ProgressRepository;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final StudentRepository  studentRepository;
    private final UserRepository     userRepository;

    public ProgressService(ProgressRepository progressRepository,
                           StudentRepository studentRepository,
                           UserRepository userRepository) {
        this.progressRepository = progressRepository;
        this.studentRepository  = studentRepository;
        this.userRepository     = userRepository;
    }

    public List<ProgressResponse> getByStudent(Long studentId) {
        return progressRepository.findByStudentId(studentId)
                .stream().map(ProgressResponse::from).toList();
    }

    public ProgressResponse upsert(ProgressRequest req, String updaterEmail) {
        Progress p = progressRepository
                .findByStudentIdAndCategory(req.studentId(), req.category())
                .orElse(new Progress());

        p.setStudent(studentRepository.findById(req.studentId())
                .orElseThrow(() -> new RuntimeException("Student not found")));
        p.setCategory(req.category());
        if (req.hoursCompleted() != null) p.setHoursCompleted(req.hoursCompleted());
        if (req.hoursRequired()  != null) p.setHoursRequired(req.hoursRequired());
        if (req.score()          != null) p.setScore(req.score());
        p.setUpdatedAt(LocalDateTime.now());
        p.setUpdatedBy(userRepository.findByEmail(updaterEmail).orElse(null));

        return ProgressResponse.from(progressRepository.save(p));
    }

    public void delete(Long id) {
        if (!progressRepository.existsById(id)) throw new RuntimeException("Progress not found");
        progressRepository.deleteById(id);
    }
}
