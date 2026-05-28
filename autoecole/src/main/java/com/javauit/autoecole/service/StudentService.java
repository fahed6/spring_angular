package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.StudentRequest;
import com.javauit.autoecole.dto.StudentResponse;
import com.javauit.autoecole.entity.Student;
import com.javauit.autoecole.entity.User;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository    userRepository;

    public StudentService(StudentRepository studentRepository, UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository    = userRepository;
    }

    public List<StudentResponse> getAll(String search) {
        List<Student> students = (search != null && !search.isBlank())
                ? studentRepository.search(search)
                : studentRepository.findAll();
        return students.stream().map(StudentResponse::from).toList();
    }

    public StudentResponse getById(Long id) {
        return StudentResponse.from(studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found")));
    }

    public StudentResponse create(StudentRequest req, String registrarEmail) {
        User registrar = userRepository.findByEmail(registrarEmail).orElse(null);
        Student s = new Student();
        applyRequest(s, req);
        s.setRegisteredBy(registrar);
        return StudentResponse.from(studentRepository.save(s));
    }

    public StudentResponse update(Long id, StudentRequest req) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        applyRequest(s, req);
        return StudentResponse.from(studentRepository.save(s));
    }

    public void delete(Long id) {
        if (!studentRepository.existsById(id)) throw new RuntimeException("Student not found");
        studentRepository.deleteById(id);
    }

    private void applyRequest(Student s, StudentRequest req) {
        if (req.firstName()   != null) s.setFirstName(req.firstName());
        if (req.lastName()    != null) s.setLastName(req.lastName());
        if (req.email()       != null) s.setEmail(req.email());
        if (req.phone()       != null) s.setPhone(req.phone());
        if (req.dateOfBirth() != null) s.setDateOfBirth(req.dateOfBirth());
        if (req.address()     != null) s.setAddress(req.address());
        if (req.status()      != null) s.setStatus(req.status());
    }
}
