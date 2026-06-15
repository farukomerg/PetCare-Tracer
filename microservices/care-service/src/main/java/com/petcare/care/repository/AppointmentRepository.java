package com.petcare.care.repository;

import com.petcare.care.dto.CreateAppointmentRequest;
import com.petcare.care.model.Appointment;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentRepository {

    private final JdbcTemplate jdbc;

    public AppointmentRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private Appointment mapRow(ResultSet rs, int rn) throws SQLException {
        return new Appointment(
                rs.getLong("appointment_id"), rs.getLong("pet_id"), rs.getLong("vet_id"),
                rs.getString("vet_name"), rs.getString("clinic_name"),
                rs.getTimestamp("appointment_time").toLocalDateTime(),
                rs.getString("status"), rs.getString("note"));
    }

    public List<Appointment> findAll() {
        return jdbc.query("SELECT * FROM appointments ORDER BY appointment_id", this::mapRow);
    }

    public Optional<Appointment> findById(Long id) {
        return jdbc.query("SELECT * FROM appointments WHERE appointment_id=?", this::mapRow, id).stream().findFirst();
    }

    public List<Appointment> findByPetId(Long petId) {
        return jdbc.query("SELECT * FROM appointments WHERE pet_id=? ORDER BY appointment_time", this::mapRow, petId);
    }
    
    public List<Appointment> findByVetId(Long vetId) {
        return jdbc.query("SELECT * FROM appointments WHERE vet_id=? ORDER BY appointment_time", this::mapRow, vetId);
    }

    public Long save(CreateAppointmentRequest r) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO appointments(pet_id,vet_id,vet_name,clinic_name,appointment_time,status,note) VALUES(?,?,?,?,?,?,?)",
                    new String[]{"appointment_id"});
            ps.setLong(1, r.petId());
            ps.setLong(2, r.vetId());
            ps.setString(3, r.vetName());
            ps.setString(4, r.clinicName());
            ps.setTimestamp(5, Timestamp.valueOf(r.appointmentTime()));
            ps.setString(6, r.status() != null ? r.status() : "PLANNED");
            ps.setString(7, r.note());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public List<Appointment> findByVetName(String vetName) {
        return jdbc.query("SELECT * FROM appointments WHERE LOWER(vet_name) LIKE LOWER(?) ORDER BY appointment_time",
                this::mapRow, "%" + vetName + "%");
    }

    public void updateStatus(Long id, String status) {
        jdbc.update("UPDATE appointments SET status=? WHERE appointment_id=?", status, id);
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM appointments WHERE appointment_id=?", id);
    }
}
