package com.petcarebackend.repository;

import com.petcarebackend.dto.health.CreateHealthRecordRequest;
import com.petcarebackend.model.HealthRecord;
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
public class HealthRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public HealthRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HealthRecord> findAll() {
        String sql = """
                SELECT health_record_id, pet_id, record_type, record_date, description, created_at
                FROM health_records
                ORDER BY health_record_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new HealthRecord(
                rs.getLong("health_record_id"),
                rs.getLong("pet_id"),
                rs.getString("record_type"),
                rs.getObject("record_date", java.time.LocalDate.class),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }

    public Optional<HealthRecord> findById(Long healthRecordId) {
        String sql = """
                SELECT health_record_id, pet_id, record_type, record_date, description, created_at
                FROM health_records
                WHERE health_record_id = ?
                """;

        List<HealthRecord> results = jdbcTemplate.query(sql, (rs, rowNum) -> new HealthRecord(
                rs.getLong("health_record_id"),
                rs.getLong("pet_id"),
                rs.getString("record_type"),
                rs.getObject("record_date", java.time.LocalDate.class),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), healthRecordId);

        return results.stream().findFirst();
    }

    public List<HealthRecord> findByPetId(Long petId) {
        String sql = """
                SELECT health_record_id, pet_id, record_type, record_date, description, created_at
                FROM health_records
                WHERE pet_id = ?
                ORDER BY health_record_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new HealthRecord(
                rs.getLong("health_record_id"),
                rs.getLong("pet_id"),
                rs.getString("record_type"),
                rs.getObject("record_date", java.time.LocalDate.class),
                rs.getString("description"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), petId);
    }

    public Long save(CreateHealthRecordRequest request) {
        String sql = """
                INSERT INTO health_records (pet_id, record_type, record_date, description)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, request.petId());
            ps.setString(2, request.recordType());
            ps.setDate(3, Date.valueOf(request.recordDate()));
            ps.setString(4, request.description());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
