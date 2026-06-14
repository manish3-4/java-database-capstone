package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {

        var validation = service.validateToken(token, "patient");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        return patientService.getPatientDetails(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createPatient(@Valid @RequestBody Patient patient) {

        if (!service.validatePatient(patient)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of(
                            "success", false,
                            "message", "Patient already exists"
                    ));
        }

        int result = patientService.createPatient(patient);

        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(java.util.Map.of(
                            "success", true,
                            "message", "Patient registered successfully"
                    ));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of(
                        "success", false,
                        "message", "Failed to register patient"
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/appointments/{id}/{token}/{user}")
    public ResponseEntity<?> getPatientAppointment(
            @PathVariable Long id,
            @PathVariable String token,
            @PathVariable String user) {

        var validation = service.validateToken(token, user);

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        return patientService.getPatientAppointment(id);
    }

    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        var validation = service.validateToken(token, "patient");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        return service.filterPatient(
                "null".equalsIgnoreCase(condition) ? null : condition,
                "null".equalsIgnoreCase(name) ? null : name,
                token
        );
    }
}