package com.petcarebackend.repository;

import com.petcarebackend.dto.feeding.CreateFeedingPlanRequest;
import com.petcarebackend.model.FeedingPlan;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class FeedingPlanRepository {

    private final JdbcTemplate jdbcTemplate;

    public FeedingPlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FeedingPlan> findAll() {
        String sql = """
                SELECT feeding_plan_id, pet_id, food_name, amount, amount_unit, meals_per_day, note, is_active
                FROM feeding_plans
                ORDER BY feeding_plan_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FeedingPlan(
                rs.getLong("feeding_plan_id"),
                rs.getLong("pet_id"),
                rs.getString("food_name"),
                rs.getBigDecimal("amount"),
                rs.getString("amount_unit"),
                rs.getInt("meals_per_day"),
                rs.getString("note"),
                rs.getBoolean("is_active")
        ));
    }

    public Optional<FeedingPlan> findById(Long feedingPlanId) {
        String sql = """
                SELECT feeding_plan_id, pet_id, food_name, amount, amount_unit, meals_per_day, note, is_active
                FROM feeding_plans
                WHERE feeding_plan_id = ?
                """;

        List<FeedingPlan> results = jdbcTemplate.query(sql, (rs, rowNum) -> new FeedingPlan(
                rs.getLong("feeding_plan_id"),
                rs.getLong("pet_id"),
                rs.getString("food_name"),
                rs.getBigDecimal("amount"),
                rs.getString("amount_unit"),
                rs.getInt("meals_per_day"),
                rs.getString("note"),
                rs.getBoolean("is_active")
        ), feedingPlanId);

        return results.stream().findFirst();
    }

    public List<FeedingPlan> findByPetId(Long petId) {
        String sql = """
                SELECT feeding_plan_id, pet_id, food_name, amount, amount_unit, meals_per_day, note, is_active
                FROM feeding_plans
                WHERE pet_id = ?
                ORDER BY feeding_plan_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FeedingPlan(
                rs.getLong("feeding_plan_id"),
                rs.getLong("pet_id"),
                rs.getString("food_name"),
                rs.getBigDecimal("amount"),
                rs.getString("amount_unit"),
                rs.getInt("meals_per_day"),
                rs.getString("note"),
                rs.getBoolean("is_active")
        ), petId);
    }

    public Long save(CreateFeedingPlanRequest request) {
        String sql = """
                INSERT INTO feeding_plans (pet_id, food_name, amount, amount_unit, meals_per_day, note, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, request.petId());
            ps.setString(2, request.foodName());
            ps.setBigDecimal(3, request.amount());
            ps.setString(4, request.amountUnit());
            ps.setInt(5, request.mealsPerDay());
            ps.setString(6, request.note());
            ps.setBoolean(7, request.isActive() != null ? request.isActive() : true);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
