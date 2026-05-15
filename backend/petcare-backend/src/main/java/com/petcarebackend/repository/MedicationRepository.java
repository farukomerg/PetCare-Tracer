package com.petcarebackend.repository;

import com.petcarebackend.dto.medication.CreateMedicationRequest;
import com.petcarebackend.model.Medication;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MedicationRepository {

    private final JdbcTemplate jdbcTemplate;

    public MedicationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Medication> findAll() {
        String sql = """
                SELECT medication_id, medication_name, form, description
                FROM medications
                ORDER BY medication_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Medication(
                rs.getLong("medication_id"),
                rs.getString("medication_name"),
                rs.getString("form"),
                rs.getString("description")
        ));
    }

    public Optional<Medication> findById(Long medicationId) {
        String sql = """
                SELECT medication_id, medication_name, form, description
                FROM medications
                WHERE medication_id = ?
                """;

        List<Medication> results = jdbcTemplate.query(sql, (rs, rowNum) -> new Medication(
                rs.getLong("medication_id"),
                rs.getString("medication_name"),
                rs.getString("form"),
                rs.getString("description")
        ), medicationId);

        return results.stream().findFirst();
    }

    public Optional<Medication> findByName(String medicationName) {
        String sql = """
                SELECT medication_id, medication_name, form, description
                FROM medications
                WHERE medication_name = ?
                """;

        List<Medication> results = jdbcTemplate.query(sql, (rs, rowNum) -> new Medication(
                rs.getLong("medication_id"),
                rs.getString("medication_name"),
                rs.getString("form"),
                rs.getString("description")
        ), medicationName);

        return results.stream().findFirst();
    }

    public Long save(CreateMedicationRequest request) {
        String sql = """
                INSERT INTO medications (medication_name, form, description)
                VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"medication_id"});
            ps.setString(1, request.medicationName());
            ps.setString(2, request.form());
            ps.setString(3, request.description());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
