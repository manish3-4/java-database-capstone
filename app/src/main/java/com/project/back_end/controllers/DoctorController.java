package com.project.back_end.controllers;

import com.project.back_end.dto.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable LocalDate date,
            @PathVariable String token) {

        Map<String, Object> validation = service.validateToken(token, user);

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        return doctorService.getDoctorAvailability(doctorId, date);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, Object>> saveDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, Object> validation = service.validateToken(token, "admin");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        Map<String, Object> response = new HashMap<>();
        int result = doctorService.saveDoctor(doctor);

        if (result == 1) {
            response.put("success", true);
            response.put("message", "Doctor added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else if (result == -1) {
            response.put("success", false);
            response.put("message", "Doctor already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        response.put("success", false);
        response.put("message", "Failed to add doctor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(
            @RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, Object>> updateDoctor(
            @Valid @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, Object> validation = service.validateToken(token, "admin");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        Map<String, Object> response = new HashMap<>();
        int result = doctorService.updateDoctor(doctor);

        if (result == 1) {
            response.put("success", true);
            response.put("message", "Doctor updated successfully");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("success", false);
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("success", false);
        response.put("message", "Failed to update doctor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, Object>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, Object> validation = service.validateToken(token, "admin");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(validation);
        }

        Map<String, Object> response = new HashMap<>();
        int result = doctorService.deleteDoctor(id);

        if (result == 1) {
            response.put("success", true);
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        } else if (result == -1) {
            response.put("success", false);
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.put("success", false);
        response.put("message", "Failed to delete doctor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        Map<String, Object> response = new HashMap<>();
        response.put("doctors",
                service.filterDoctor(
                        "null".equalsIgnoreCase(name) ? null : name,
                        "null".equalsIgnoreCase(time) ? null : time,
                        "null".equalsIgnoreCase(speciality) ? null : speciality
                )
        );

        return ResponseEntity.ok(response);
    }
}