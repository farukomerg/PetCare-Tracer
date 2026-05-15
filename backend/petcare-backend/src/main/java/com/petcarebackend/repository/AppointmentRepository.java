package com.petcarebackend.repository;

import com.petcarebackend.dto.appointment.CreateAppointmentRequest;
import com.petcarebackend.model.Appointment;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    public AppointmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Appointment> findAll() {
        String sql = """
                SELECT appointment_id, pet_id, vet_name, clinic_name, appointment_time, status, note
                FROM appointments
                ORDER BY appointment_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Appointment(
                rs.getLong("appointment_id"),
                rs.getLong("pet_id"),
                rs.getString("vet_name"),
                rs.getString("clinic_name"),
                rs.getTimestamp("appointment_time").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("note")
        ));
    }

    public Optional<Appointment> findById(Long appointmentId) {
        String sql = """
                SELECT appointment_id, pet_id, vet_name, clinic_name, appointment_time, status, note
                FROM appointments
                WHERE appointment_id = ?
                """;

        List<Appointment> results = jdbcTemplate.query(sql, (rs, rowNum) -> new Appointment(
                rs.getLong("appointment_id"),
                rs.getLong("pet_id"),
                rs.getString("vet_name"),
                rs.getString("clinic_name"),
                rs.getTimestamp("appointment_time").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("note")
        ), appointmentId);

        return results.stream().findFirst();
    }

    public List<Appointment> findByPetId(Long petId) {
        String sql = """
                SELECT appointment_id, pet_id, vet_name, clinic_name, appointment_time, status, note
                FROM appointments
                WHERE pet_id = ?
                ORDER BY appointment_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Appointment(
                rs.getLong("appointment_id"),
                rs.getLong("pet_id"),
                rs.getString("vet_name"),
                rs.getString("clinic_name"),
                rs.getTimestamp("appointment_time").toLocalDateTime(),
                rs.getString("status"),
                rs.getString("note")
        ), petId);
    }

    public Long save(CreateAppointmentRequest request) {
        String sql = """
                INSERT INTO appointments (pet_id, vet_name, clinic_name, appointment_time, status, note)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, request.petId());
            ps.setString(2, request.vetName());
            ps.setString(3, request.clinicName());
            ps.setTimestamp(4, Timestamp.valueOf(request.appointmentTime()));
            ps.setString(5, request.status());
            ps.setString(6, request.note());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
