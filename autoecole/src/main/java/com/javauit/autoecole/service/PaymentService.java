package com.javauit.autoecole.service;

import com.javauit.autoecole.dto.PaymentRequest;
import com.javauit.autoecole.dto.PaymentResponse;
import com.javauit.autoecole.entity.Payment;
import com.javauit.autoecole.repository.PaymentRepository;
import com.javauit.autoecole.repository.StudentRepository;
import com.javauit.autoecole.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository    userRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          StudentRepository studentRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.userRepository    = userRepository;
    }

    public List<PaymentResponse> getAll(Long studentId) {
        List<Payment> list = studentId != null
                ? paymentRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                : paymentRepository.findAllByOrderByCreatedAtDesc();
        return list.stream().map(PaymentResponse::from).toList();
    }

    public PaymentResponse getById(Long id) {
        return PaymentResponse.from(findOrThrow(id));
    }

    public PaymentResponse create(PaymentRequest req, String recorderEmail) {
        Payment p = new Payment();
        applyRequest(p, req);
        p.setRecordedBy(userRepository.findByEmail(recorderEmail).orElse(null));
        return PaymentResponse.from(paymentRepository.save(p));
    }

    public PaymentResponse update(Long id, PaymentRequest req) {
        Payment p = findOrThrow(id);
        applyRequest(p, req);
        return PaymentResponse.from(paymentRepository.save(p));
    }

    public void delete(Long id) {
        if (!paymentRepository.existsById(id)) throw new RuntimeException("Payment not found");
        paymentRepository.deleteById(id);
    }

    private void applyRequest(Payment p, PaymentRequest req) {
        if (req.studentId()   != null)
            p.setStudent(studentRepository.findById(req.studentId())
                    .orElseThrow(() -> new RuntimeException("Student not found")));
        if (req.amount()      != null) p.setAmount(req.amount());
        if (req.paidAt()      != null) p.setPaidAt(req.paidAt());
        if (req.method()      != null) p.setMethod(req.method());
        if (req.status()      != null) p.setStatus(req.status());
        if (req.description() != null) p.setDescription(req.description());
    }

    private Payment findOrThrow(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}
