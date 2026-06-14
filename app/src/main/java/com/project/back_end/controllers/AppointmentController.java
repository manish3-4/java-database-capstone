package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(
            AppointmentService appointmentService,
            Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(
            @PathVariable String date,
            @PathVariable String patientName,
            @PathVariable String token) {

        Map<String, String> validation =
                service.validateToken(token, "doctor");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(validation);
        }

        return appointmentService.getAppointments(
                date,
                patientName,
                token);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> validation =
                service.validateToken(token, "patient");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(validation);
        }

        int result = service.validateAppointment(
                appointment.getDoctorId(),
                appointment.getAppointmentTime().toLocalDate(),
                appointment.getAppointmentTime());

        if (result == -1) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Invalid doctor id"));
        }

        if (result == 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Selected appointment slot is unavailable"));
        }

        int booked =
                appointmentService.bookAppointment(
                        appointment,
                        token);

        if (booked == 1) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message",
                            "Appointment booked successfully"));
        }

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message",
                        "Unable to book appointment"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(
            @RequestBody Appointment appointment,
            @PathVariable String token) {

        Map<String, String> validation =
                service.validateToken(token, "patient");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(validation);
        }

        return appointmentService.updateAppointment(
                appointment,
                token);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, String> validation =
                service.validateToken(token, "patient");

        if (!validation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(validation);
        }

        return appointmentService.cancelAppointment(
                id,
                token);
    }
}