package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.PlanningRequest;
import com.javauit.autoecole.dto.PlanningResponse;
import com.javauit.autoecole.entity.Planning;
import com.javauit.autoecole.repository.PlanningRepository;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanningService {

    private final PlanningRepository planningRepository;
    private final StudentRepository  studentRepository;
    private final UserRepository     userRepository;

    public PlanningService(PlanningRepository planningRepository,
                           StudentRepository studentRepository,
                           UserRepository userRepository) {
        this.planningRepository = planningRepository;
        this.studentRepository  = studentRepository;
        this.userRepository     = userRepository;
    }

    public List<PlanningResponse> getAll(Long studentId) {
        List<Planning> list = studentId != null
                ? planningRepository.findByStudentIdOrderByScheduledAtDesc(studentId)
                : planningRepository.findAllByOrderByScheduledAtDesc();
        return list.stream().map(PlanningResponse::from).toList();
    }

    public PlanningResponse getById(Long id) {
        return PlanningResponse.from(findOrThrow(id));
    }

    public PlanningResponse create(PlanningRequest req) {
        Planning p = new Planning();
        applyRequest(p, req);
        return PlanningResponse.from(planningRepository.save(p));
    }

    public PlanningResponse update(Long id, PlanningRequest req) {
        Planning p = findOrThrow(id);
        applyRequest(p, req);
        return PlanningResponse.from(planningRepository.save(p));
    }

    public void delete(Long id) {
        if (!planningRepository.existsById(id)) throw new RuntimeException("Planning not found");
        planningRepository.deleteById(id);
    }

    private void applyRequest(Planning p, PlanningRequest req) {
        if (req.studentId() != null)
            p.setStudent(studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new RuntimeException("Student not found")));
        if (req.instructorId() != null)
            p.setInstructor(userRepository.findById(req.instructorId()).orElse(null));
        if (req.scheduledAt() != null) p.setScheduledAt(req.scheduledAt());
        if (req.type()        != null) p.setType(req.type());
        if (req.status()      != null) p.setStatus(req.status());
        if (req.notes()       != null) p.setNotes(req.notes());
    }

    private Planning findOrThrow(Long id) {
        return planningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planning not found"));
    }
}
