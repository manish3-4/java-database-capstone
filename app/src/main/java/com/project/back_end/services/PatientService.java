package com.project.back_end.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            TokenService tokenService) {

        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Create Patient
     */
    public int createPatient(Patient patient) {

        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Get Patient Appointments
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {

        try {

            List<Appointment> appointments =
                    appointmentRepository.findByPatientId(patientId);

            List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

            for (Appointment appointment : appointments) {

                appointmentDTOs.add(
                        new AppointmentDTO(
                                appointment.getId(),
                                appointment.getDoctor().getId(),
                                appointment.getDoctor().getName(),
                                appointment.getPatient().getId(),
                                appointment.getPatient().getName(),
                                appointment.getPatient().getEmail(),
                                appointment.getPatient().getPhone(),
                                appointment.getPatient().getAddress(),
                                appointment.getAppointmentTime(),
                                appointment.getStatus()
                        )
                );
            }

            return appointmentDTOs;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Filter By Condition
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByCondition(
            Long patientId,
            String condition) {

        try {

            int status;

            if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else {
                return new ArrayList<>();
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(
                                    patientId,
                                    status);

            List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

            for (Appointment appointment : appointments) {

                appointmentDTOs.add(
                        new AppointmentDTO(
                                appointment.getId(),
                                appointment.getDoctor().getId(),
                                appointment.getDoctor().getName(),
                                appointment.getPatient().getId(),
                                appointment.getPatient().getName(),
                                appointment.getPatient().getEmail(),
                                appointment.getPatient().getPhone(),
                                appointment.getPatient().getAddress(),
                                appointment.getAppointmentTime(),
                                appointment.getStatus()
                        )
                );
            }

            return appointmentDTOs;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Filter By Doctor
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctor(
            Long patientId,
            String doctorName) {

        try {

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientId(
                                    doctorName,
                                    patientId);

            List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

            for (Appointment appointment : appointments) {

                appointmentDTOs.add(
                        new AppointmentDTO(
                                appointment.getId(),
                                appointment.getDoctor().getId(),
                                appointment.getDoctor().getName(),
                                appointment.getPatient().getId(),
                                appointment.getPatient().getName(),
                                appointment.getPatient().getEmail(),
                                appointment.getPatient().getPhone(),
                                appointment.getPatient().getAddress(),
                                appointment.getAppointmentTime(),
                                appointment.getStatus()
                        )
                );
            }

            return appointmentDTOs;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Filter By Doctor And Condition
     */
    @Transactional(readOnly = true)
    public List<AppointmentDTO> filterByDoctorAndCondition(
            Long patientId,
            String doctorName,
            String condition) {

        try {

            int status;

            if ("future".equalsIgnoreCase(condition)) {
                status = 0;
            } else if ("past".equalsIgnoreCase(condition)) {
                status = 1;
            } else {
                return new ArrayList<>();
            }

            List<Appointment> appointments =
                    appointmentRepository
                            .filterByDoctorNameAndPatientIdAndStatus(
                                    doctorName,
                                    patientId,
                                    status);

            List<AppointmentDTO> appointmentDTOs = new ArrayList<>();

            for (Appointment appointment : appointments) {

                appointmentDTOs.add(
                        new AppointmentDTO(
                                appointment.getId(),
                                appointment.getDoctor().getId(),
                                appointment.getDoctor().getName(),
                                appointment.getPatient().getId(),
                                appointment.getPatient().getName(),
                                appointment.getPatient().getEmail(),
                                appointment.getPatient().getPhone(),
                                appointment.getPatient().getAddress(),
                                appointment.getAppointmentTime(),
                                appointment.getStatus()
                        )
                );
            }

            return appointmentDTOs;

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Get Patient Details
     */
    public Patient getPatientDetails(String token) {

        try {

            String email = tokenService.extractUsername(token);

            return patientRepository.findByEmail(email);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}