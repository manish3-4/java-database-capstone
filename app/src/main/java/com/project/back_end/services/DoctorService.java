package com.project.back_end.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getDoctorAvailability(Long doctorId, LocalDate date) {

        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

        if (doctor == null) {
            return new ArrayList<>();
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Appointment> appointments =
                appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                        doctorId, start, end);

        List<LocalTime> bookedSlots = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime())
                .toList();

        List<LocalTime> availableSlots = new ArrayList<>();

        for (LocalTime time : doctor.getAvailableTimes()) {
            if (!bookedSlots.contains(time)) {
                availableSlots.add(time);
            }
        }

        return availableSlots;
    }

    public int saveDoctor(Doctor doctor) {

        try {

            Doctor existingDoctor =
                    doctorRepository.findByEmail(doctor.getEmail());

            if (existingDoctor != null) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {

        try {

            if (!doctorRepository.existsById(doctor.getId())) {
                return -1;
            }

            doctorRepository.save(doctor);
            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(Long doctorId) {

        try {

            Doctor doctor = doctorRepository.findById(doctorId).orElse(null);

            if (doctor == null) {
                return -1;
            }

            appointmentRepository.deleteAllByDoctorId(doctorId);
            doctorRepository.delete(doctor);

            return 1;

        } catch (Exception e) {
            return 0;
        }
    }

    public Object validateDoctor(Login login) {

        Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());

        if (doctor == null) {
            return "Doctor not found";
        }

        if (!doctor.getPassword().equals(login.getPassword())) {
            return "Invalid password";
        }

        return tokenService.generateToken(
                doctor.getId(),
                doctor.getEmail(),
                "doctor"
        );
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByNameSpecilityandTime(
            String name,
            String specialty,
            String time) {

        List<Doctor> doctors =
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name,
                        specialty);

        return filterDoctorByTime(doctors, time);
    }

    public List<Doctor> filterDoctorByTime(
            List<Doctor> doctors,
            String time) {

        List<Doctor> filteredDoctors = new ArrayList<>();

        for (Doctor doctor : doctors) {

            boolean matched = doctor.getAvailableTimes().stream().anyMatch(slot ->
                    ("AM".equalsIgnoreCase(time) && slot.getHour() < 12)
                            || ("PM".equalsIgnoreCase(time) && slot.getHour() >= 12));

            if (matched) {
                filteredDoctors.add(doctor);
            }
        }

        return filteredDoctors;
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndTime(
            String name,
            String time) {

        List<Doctor> doctors =
                doctorRepository.findByNameLike(name);

        return filterDoctorByTime(doctors, time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByNameAndSpecility(
            String name,
            String specialty) {

        return doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
                        name,
                        specialty);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorByTimeAndSpecility(
            String specialty,
            String time) {

        List<Doctor> doctors =
                doctorRepository.findBySpecialtyIgnoreCase(specialty);

        return filterDoctorByTime(doctors, time);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorBySpecility(String specialty) {

        return doctorRepository
                .findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional(readOnly = true)
    public List<Doctor> filterDoctorsByTime(String time) {

        List<Doctor> doctors = doctorRepository.findAll();

        return filterDoctorByTime(doctors, time);
    }
}