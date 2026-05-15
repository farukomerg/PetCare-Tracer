package com.petcarebackend.repository;

import com.petcarebackend.dto.pet.CreatePetRequest;
import com.petcarebackend.dto.pet.UpdatePetRequest;
import com.petcarebackend.model.Pet;
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
public class PetRepository {

    private final JdbcTemplate jdbcTemplate;

    public PetRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Pet> findAll() {
        String sql = """
                SELECT pet_id, user_id, pet_name, species, breed, gender, birth_date, current_weight, notes, created_at
                FROM pets
                ORDER BY pet_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Pet(
                rs.getLong("pet_id"),
                rs.getLong("user_id"),
                rs.getString("pet_name"),
                rs.getString("species"),
                rs.getString("breed"),
                rs.getString("gender"),
                rs.getObject("birth_date", java.time.LocalDate.class),
                rs.getBigDecimal("current_weight"),
                rs.getString("notes"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }

    public Optional<Pet> findById(Long petId) {
        String sql = """
                SELECT pet_id, user_id, pet_name, species, breed, gender, birth_date, current_weight, notes, created_at
                FROM pets
                WHERE pet_id = ?
                """;
        List<Pet> pets = jdbcTemplate.query(sql, (rs, rowNum) -> new Pet(
                rs.getLong("pet_id"),
                rs.getLong("user_id"),
                rs.getString("pet_name"),
                rs.getString("species"),
                rs.getString("breed"),
                rs.getString("gender"),
                rs.getObject("birth_date", java.time.LocalDate.class),
                rs.getBigDecimal("current_weight"),
                rs.getString("notes"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), petId);
        return pets.stream().findFirst();
    }

    public List<Pet> findByUserId(Long userId) {
        String sql = """
                SELECT pet_id, user_id, pet_name, species, breed, gender, birth_date, current_weight, notes, created_at
                FROM pets
                WHERE user_id = ?
                ORDER BY pet_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Pet(
                rs.getLong("pet_id"),
                rs.getLong("user_id"),
                rs.getString("pet_name"),
                rs.getString("species"),
                rs.getString("breed"),
                rs.getString("gender"),
                rs.getObject("birth_date", java.time.LocalDate.class),
                rs.getBigDecimal("current_weight"),
                rs.getString("notes"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), userId);
    }

    public Long save(CreatePetRequest request) {
        String sql = """
                INSERT INTO pets (user_id, pet_name, species, breed, gender, birth_date, current_weight, notes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"pet_id"});
            ps.setLong(1, request.userId());
            ps.setString(2, request.petName());
            ps.setString(3, request.species());
            ps.setString(4, request.breed());
            ps.setString(5, request.gender());
            ps.setDate(6, request.birthDate() != null ? Date.valueOf(request.birthDate()) : null);
            ps.setBigDecimal(7, request.currentWeight());
            ps.setString(8, request.notes());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int update(Long petId, UpdatePetRequest request) {
        String sql = """
                UPDATE pets
                SET user_id = ?, pet_name = ?, species = ?, breed = ?, gender = ?,
                    birth_date = ?, current_weight = ?, notes = ?
                WHERE pet_id = ?
                """;

        return jdbcTemplate.update(sql,
                request.userId(),
                request.petName(),
                request.species(),
                request.breed(),
                request.gender(),
                request.birthDate() != null ? Date.valueOf(request.birthDate()) : null,
                request.currentWeight(),
                request.notes(),
                petId
        );
    }

    public int deleteById(Long petId) {
        String sql = "DELETE FROM pets WHERE pet_id = ?";
        return jdbcTemplate.update(sql, petId);
    }
}
