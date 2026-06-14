package com.project.back_end.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.project.back_end.entity.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
@Service
public class AppointmentService {

    // Dependencies
    private final AppointmentRepository appointmentRepository;
    private final Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    // Constructor Injection
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            Service service,
            TokenService tokenService,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Book Appointment
     * Returns:
     * 1 -> Success
     * 0 -> Failure
     */
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Update Appointment
     */
    @Transactional
    public String updateAppointment(Long appointmentId,
                                    Long patientId,
                                    Appointment updatedAppointment) {

        try {
            Appointment existingAppointment =
                    appointmentRepository.findById(appointmentId).orElse(null);

            if (existingAppointment == null) {
                return "Appointment not found";
            }

            if (!existingAppointment.getPatient().getId().equals(patientId)) {
                return "Unauthorized access";
            }

            // Additional doctor availability validation can be added here

            existingAppointment.setAppointmentTime(
                    updatedAppointment.getAppointmentTime());

            existingAppointment.setDoctor(
                    updatedAppointment.getDoctor());

            appointmentRepository.save(existingAppointment);

            return "Appointment updated successfully";

        } catch (Exception e) {
            return "Failed to update appointment";
        }
    }

    /**
     * Cancel Appointment
     */
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {

        try {
            Appointment appointment =
                    appointmentRepository.findById(appointmentId).orElse(null);

            if (appointment == null) {
                return "Appointment not found";
            }

            if (!appointment.getPatient().getId().equals(patientId)) {
                return "Unauthorized access";
            }

            appointmentRepository.delete(appointment);

            return "Appointment cancelled successfully";

        } catch (Exception e) {
            return "Failed to cancel appointment";
        }
    }

    /**
     * Get Doctor Appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointments(Long doctorId,
                                             String patientName,
                                             LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        if (patientName == null || patientName.equalsIgnoreCase("null")
                || patientName.isBlank()) {

            return appointmentRepository
                    .findByDoctorIdAndAppointmentTimeBetween(
                            doctorId, start, end);
        }

        return appointmentRepository
                .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                        doctorId,
                        patientName,
                        start,
                        end);
    }

    /**
     * Change Appointment Status
     */
    @Transactional
    public void changeStatus(int status, long appointmentId) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}