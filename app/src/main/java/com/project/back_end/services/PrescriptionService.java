package com.project.back_end.services;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();

        try {
            List<Prescription> existingPrescriptions =
                    prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());

            if (existingPrescriptions != null && !existingPrescriptions.isEmpty()) {
                response.put("message", "Prescription already exists for this appointment");
                return ResponseEntity.badRequest().body(response);
            }

            prescriptionRepository.save(prescription);

            response.put("message", "Prescription saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("message", "Failed to save prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Prescription> prescriptions =
                    prescriptionRepository.findByAppointmentId(appointmentId);

            if (prescriptions == null || prescriptions.isEmpty()) {
                response.put("message", "No prescription found");
                return ResponseEntity.ok(response);
            }

            response.put("prescription", prescriptions.get(0));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Failed to fetch prescription");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}