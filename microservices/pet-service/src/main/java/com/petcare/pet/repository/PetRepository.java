package com.petcare.pet.repository;

import com.petcare.pet.dto.CreatePetRequest;
import com.petcare.pet.dto.UpdatePetRequest;
import com.petcare.pet.model.Pet;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PetRepository {

    private final JdbcTemplate jdbc;

    public PetRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private Pet mapRow(ResultSet rs, int rn) throws SQLException {
        return new Pet(
                rs.getLong("pet_id"), rs.getLong("user_id"),
                rs.getString("pet_name"), rs.getString("species"),
                rs.getString("breed"), rs.getString("gender"),
                rs.getObject("birth_date", java.time.LocalDate.class),
                rs.getBigDecimal("current_weight"), rs.getString("notes"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }

    private static final String SELECT =
            "SELECT pet_id, user_id, pet_name, species, breed, gender, birth_date, current_weight, notes, created_at FROM pets";

    public List<Pet> findAll() {
        return jdbc.query(SELECT + " ORDER BY pet_id", this::mapRow);
    }

    public Optional<Pet> findById(Long id) {
        return jdbc.query(SELECT + " WHERE pet_id=?", this::mapRow, id).stream().findFirst();
    }

    public List<Pet> findByUserId(Long uid) {
        return jdbc.query(SELECT + " WHERE user_id=? ORDER BY pet_id", this::mapRow, uid);
    }

    public Long save(CreatePetRequest r) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO pets(user_id,pet_name,species,breed,gender,birth_date,current_weight,notes) VALUES(?,?,?,?,?,?,?,?)",
                    new String[]{"pet_id"});
            ps.setLong(1, r.userId()); ps.setString(2, r.petName()); ps.setString(3, r.species());
            ps.setString(4, r.breed()); ps.setString(5, r.gender());
            ps.setDate(6, r.birthDate() != null ? Date.valueOf(r.birthDate()) : null);
            ps.setBigDecimal(7, r.currentWeight()); ps.setString(8, r.notes());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public int update(Long id, UpdatePetRequest r) {
        return jdbc.update(
                "UPDATE pets SET user_id=?,pet_name=?,species=?,breed=?,gender=?,birth_date=?,current_weight=?,notes=? WHERE pet_id=?",
                r.userId(), r.petName(), r.species(), r.breed(), r.gender(),
                r.birthDate() != null ? Date.valueOf(r.birthDate()) : null,
                r.currentWeight(), r.notes(), id);
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM pets WHERE pet_id=?", id);
    }
}
