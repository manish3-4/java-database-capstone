package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service; // your token/role service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service; // token validation + role check

    // 3. Save Prescription
    @PostMapping("/save/{token}")
    public ResponseEntity<?> savePrescription(
            @RequestBody Prescription prescription,
            @PathVariable String token) {

        // Validate doctor role
        boolean isValid = service.validateToken(token, "doctor");

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token or unauthorized access");
        }

        try {
            // Update appointment status first
            appointmentService.updateStatus(
                    prescription.getAppointmentId(),
                    "PRESCRIPTION_ADDED"
            );

            // Save prescription
            Prescription saved = prescriptionService.savePrescription(prescription);

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving prescription: " + e.getMessage());
        }
    }

    // 4. Get Prescription by Appointment ID
    @GetMapping("/get/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        // Validate doctor role
        boolean isValid = service.validateToken(token, "doctor");

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid token or unauthorized access");
        }

        try {
            Prescription prescription =
                    prescriptionService.getByAppointmentId(appointmentId);

            if (prescription == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Prescription not found for appointment ID: " + appointmentId);
            }

            return ResponseEntity.ok(prescription);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching prescription: " + e.getMessage());
        }
    }
}