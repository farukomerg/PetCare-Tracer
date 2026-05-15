package com.petcarebackend.repository;

import com.petcarebackend.dto.vaccine.CreateVaccineRequest;
import com.petcarebackend.model.Vaccine;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class VaccineRepository {

    private final JdbcTemplate jdbcTemplate;

    public VaccineRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Vaccine> findAll() {
        String sql = """
                SELECT vaccine_id, vaccine_name, description, repeat_days
                FROM vaccines
                ORDER BY vaccine_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Vaccine(
                rs.getLong("vaccine_id"),
                rs.getString("vaccine_name"),
                rs.getString("description"),
                rs.getInt("repeat_days")
        ));
    }

    public Optional<Vaccine> findById(Long vaccineId) {
        String sql = """
                SELECT vaccine_id, vaccine_name, description, repeat_days
                FROM vaccines
                WHERE vaccine_id = ?
                """;

        List<Vaccine> results = jdbcTemplate.query(sql, (rs, rowNum) -> new Vaccine(
                rs.getLong("vaccine_id"),
                rs.getString("vaccine_name"),
                rs.getString("description"),
                rs.getInt("repeat_days")
        ), vaccineId);

        return results.stream().findFirst();
    }

    public Optional<Vaccine> findByName(String vaccineName) {
        String sql = """
                SELECT vaccine_id, vaccine_name, description, repeat_days
                FROM vaccines
                WHERE vaccine_name = ?
                """;

        List<Vaccine> results = jdbcTemplate.query(sql, (rs, rowNum) -> new Vaccine(
                rs.getLong("vaccine_id"),
                rs.getString("vaccine_name"),
                rs.getString("description"),
                rs.getInt("repeat_days")
        ), vaccineName);

        return results.stream().findFirst();
    }

    public Long save(CreateVaccineRequest request) {
        String sql = """
                INSERT INTO vaccines (vaccine_name, description, repeat_days)
                VALUES (?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"vaccine_id"});
            ps.setString(1, request.vaccineName());
            ps.setString(2, request.description());
            if (request.repeatDays() != null) {
                ps.setInt(3, request.repeatDays());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
