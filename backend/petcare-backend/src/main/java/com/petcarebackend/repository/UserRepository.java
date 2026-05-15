package com.petcarebackend.repository;

import com.petcarebackend.dto.user.CreateUserRequest;
import com.petcarebackend.dto.user.UpdateUserRequest;
import com.petcarebackend.model.User;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {
        String sql = """
                SELECT user_id, full_name, email, password_hash, phone, created_at, is_active
                FROM users
                ORDER BY user_id
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        ));
    }

    public Optional<User> findById(Long userId) {
        String sql = """
                SELECT user_id, full_name, email, password_hash, phone, created_at, is_active
                FROM users
                WHERE user_id = ?
                """;
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        ), userId);
        return users.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = """
                SELECT user_id, full_name, email, password_hash, phone, created_at, is_active
                FROM users
                WHERE email = ?
                """;
        List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> new User(
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        ), email);
        return users.stream().findFirst();
    }

    public Long save(CreateUserRequest request, String passwordHash) {
        String sql = """
                INSERT INTO users (full_name, email, password_hash, phone)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, request.fullName());
            ps.setString(2, request.email());
            ps.setString(3, passwordHash);
            ps.setString(4, request.phone());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int update(Long userId, UpdateUserRequest request) {
        String sql = """
                UPDATE users
                SET full_name = ?, email = ?, phone = ?, is_active = ?
                WHERE user_id = ?
                """;

        return jdbcTemplate.update(sql,
                request.fullName(),
                request.email(),
                request.phone(),
                request.isActive(),
                userId
        );
    }

    public int deleteById(Long userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }
}
