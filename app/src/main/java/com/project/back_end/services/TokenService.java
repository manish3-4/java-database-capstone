package com.project.back_end.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    public TokenService(
            AdminRepository adminRepository,
            DoctorRepository doctorRepository,
            PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(String email, String role) {

        Date now = new Date();
        Date expiryDate =
                new Date(now.getTime() + (7L * 24 * 60 * 60 * 1000));

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith((SecretKey) getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {

        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String extractUsername(String token) {
        return extractEmail(token);
    }

    public Map<String, String> validateToken(String token, String role) {

        Map<String, String> response = new HashMap<>();

        try {

            String email = extractEmail(token);

            switch (role.toLowerCase()) {

                case "admin":
                    Admin admin =
                            adminRepository.findByUsername(email);

                    if (admin == null) {
                        response.put("message", "Invalid admin token");
                    }
                    break;

                case "doctor":
                    Doctor doctor =
                            doctorRepository.findByEmail(email);

                    if (doctor == null) {
                        response.put("message", "Invalid doctor token");
                    }
                    break;

                case "patient":
                    Patient patient =
                            patientRepository.findByEmail(email);

                    if (patient == null) {
                        response.put("message", "Invalid patient token");
                    }
                    break;

                default:
                    response.put("message", "Invalid role");
            }

        } catch (Exception e) {
            response.put("message", "Invalid or expired token");
        }

        return response;
    }
}