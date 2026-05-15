package com.petcarebackend.repository;

import com.petcarebackend.dto.medication.CreateMedicationScheduleRequest;
import com.petcarebackend.model.MedicationSchedule;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class MedicationScheduleRepository {

    private final JdbcTemplate jdbcTemplate;

    public MedicationScheduleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MedicationSchedule> findAll() {
        String sql = """
                SELECT medication_schedule_id, pet_id, medication_id, dosage_amount, dosage_unit,
                       frequency_per_day, start_date, end_date, status, note
                FROM medication_schedules
                ORDER BY medication_schedule_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MedicationSchedule(
                rs.getLong("medication_schedule_id"),
                rs.getLong("pet_id"),
                rs.getLong("medication_id"),
                rs.getBigDecimal("dosage_amount"),
                rs.getString("dosage_unit"),
                rs.getInt("frequency_per_day"),
                rs.getObject("start_date", java.time.LocalDate.class),
                rs.getObject("end_date", java.time.LocalDate.class),
                rs.getString("status"),
                rs.getString("note")
        ));
    }

    public Optional<MedicationSchedule> findById(Long medicationScheduleId) {
        String sql = """
                SELECT medication_schedule_id, pet_id, medication_id, dosage_amount, dosage_unit,
                       frequency_per_day, start_date, end_date, status, note
                FROM medication_schedules
                WHERE medication_schedule_id = ?
                """;

        List<MedicationSchedule> results = jdbcTemplate.query(sql, (rs, rowNum) -> new MedicationSchedule(
                rs.getLong("medication_schedule_id"),
                rs.getLong("pet_id"),
                rs.getLong("medication_id"),
                rs.getBigDecimal("dosage_amount"),
                rs.getString("dosage_unit"),
                rs.getInt("frequency_per_day"),
                rs.getObject("start_date", java.time.LocalDate.class),
                rs.getObject("end_date", java.time.LocalDate.class),
                rs.getString("status"),
                rs.getString("note")
        ), medicationScheduleId);

        return results.stream().findFirst();
    }

    public List<MedicationSchedule> findByPetId(Long petId) {
        String sql = """
                SELECT medication_schedule_id, pet_id, medication_id, dosage_amount, dosage_unit,
                       frequency_per_day, start_date, end_date, status, note
                FROM medication_schedules
                WHERE pet_id = ?
                ORDER BY medication_schedule_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new MedicationSchedule(
                rs.getLong("medication_schedule_id"),
                rs.getLong("pet_id"),
                rs.getLong("medication_id"),
                rs.getBigDecimal("dosage_amount"),
                rs.getString("dosage_unit"),
                rs.getInt("frequency_per_day"),
                rs.getObject("start_date", java.time.LocalDate.class),
                rs.getObject("end_date", java.time.LocalDate.class),
                rs.getString("status"),
                rs.getString("note")
        ), petId);
    }

    public Long save(CreateMedicationScheduleRequest request) {
        String sql = """
                INSERT INTO medication_schedules (
                    pet_id, medication_id, dosage_amount, dosage_unit, frequency_per_day,
                    start_date, end_date, status, note
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, request.petId());
            ps.setLong(2, request.medicationId());
            ps.setBigDecimal(3, request.dosageAmount());
            ps.setString(4, request.dosageUnit());
            ps.setInt(5, request.frequencyPerDay());
            ps.setDate(6, Date.valueOf(request.startDate()));
            if (request.endDate() != null) {
                ps.setDate(7, Date.valueOf(request.endDate()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            ps.setString(8, request.status());
            ps.setString(9, request.note());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
