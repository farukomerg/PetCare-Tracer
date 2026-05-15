package com.petcarebackend.repository;

import com.petcarebackend.dto.vaccine.CreateVaccineRecordRequest;
import com.petcarebackend.model.VaccineRecord;
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
public class VaccineRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public VaccineRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<VaccineRecord> findAll() {
        String sql = """
                SELECT vaccine_record_id, pet_id, vaccine_id, application_date, next_due_date, note
                FROM vaccine_records
                ORDER BY vaccine_record_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new VaccineRecord(
                rs.getLong("vaccine_record_id"),
                rs.getLong("pet_id"),
                rs.getLong("vaccine_id"),
                rs.getObject("application_date", java.time.LocalDate.class),
                rs.getObject("next_due_date", java.time.LocalDate.class),
                rs.getString("note")
        ));
    }

    public Optional<VaccineRecord> findById(Long vaccineRecordId) {
        String sql = """
                SELECT vaccine_record_id, pet_id, vaccine_id, application_date, next_due_date, note
                FROM vaccine_records
                WHERE vaccine_record_id = ?
                """;

        List<VaccineRecord> results = jdbcTemplate.query(sql, (rs, rowNum) -> new VaccineRecord(
                rs.getLong("vaccine_record_id"),
                rs.getLong("pet_id"),
                rs.getLong("vaccine_id"),
                rs.getObject("application_date", java.time.LocalDate.class),
                rs.getObject("next_due_date", java.time.LocalDate.class),
                rs.getString("note")
        ), vaccineRecordId);

        return results.stream().findFirst();
    }

    public List<VaccineRecord> findByPetId(Long petId) {
        String sql = """
                SELECT vaccine_record_id, pet_id, vaccine_id, application_date, next_due_date, note
                FROM vaccine_records
                WHERE pet_id = ?
                ORDER BY vaccine_record_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new VaccineRecord(
                rs.getLong("vaccine_record_id"),
                rs.getLong("pet_id"),
                rs.getLong("vaccine_id"),
                rs.getObject("application_date", java.time.LocalDate.class),
                rs.getObject("next_due_date", java.time.LocalDate.class),
                rs.getString("note")
        ), petId);
    }

    public Long save(CreateVaccineRecordRequest request) {
        String sql = """
                INSERT INTO vaccine_records (pet_id, vaccine_id, application_date, next_due_date, note)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"vaccine_record_id"});
            ps.setLong(1, request.petId());
            ps.setLong(2, request.vaccineId());
            ps.setDate(3, Date.valueOf(request.applicationDate()));
            if (request.nextDueDate() != null) {
                ps.setDate(4, Date.valueOf(request.nextDueDate()));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }
            ps.setString(5, request.note());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
