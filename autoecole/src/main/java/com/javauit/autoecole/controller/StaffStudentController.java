package com.javauit.autoecole.controller;

import com.javauit.autoecole.dto.StudentResponse;
import com.javauit.autoecole.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Staff — Étudiants", description = "Consultation des étudiants (lecture seule pour le staff)")
@RestController
@RequestMapping("/api/staff/students")
public class StaffStudentController {

    private final StudentService studentService;

    public StaffStudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Lister les étudiants", description = "Lecture seule — le staff peut consulter mais pas modifier")
    @ApiResponse(responseCode = "200", description = "Liste retournée avec succès")
    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAll(
            @Parameter(description = "Recherche sur le nom, prénom ou email") @RequestParam(required = false) String search) {
        return ResponseEntity.ok(studentService.getAll(search));
    }

    @Operation(summary = "Obtenir un étudiant par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Étudiant trouvé"),
        @ApiResponse(responseCode = "404", description = "Étudiant introuvable")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(
            @Parameter(description = "Identifiant de l'étudiant", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }
}
