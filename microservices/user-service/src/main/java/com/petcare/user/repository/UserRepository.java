package com.petcare.user.repository;

import com.petcare.user.dto.CreateUserRequest;
import com.petcare.user.dto.UpdateUserRequest;
import com.petcare.user.model.User;
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
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private User mapRow(ResultSet rs, int rn) throws SQLException {
        return new User(
                rs.getLong("user_id"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getString("phone"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getBoolean("is_active")
        );
    }

    private static final String SELECT_ALL =
            "SELECT user_id, full_name, email, password_hash, phone, created_at, is_active FROM users";

    public List<User> findAll() {
        return jdbc.query(SELECT_ALL + " ORDER BY user_id", this::mapRow);
    }

    public Optional<User> findById(Long id) {
        return jdbc.query(SELECT_ALL + " WHERE user_id = ?", this::mapRow, id).stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return jdbc.query(SELECT_ALL + " WHERE email = ?", this::mapRow, email).stream().findFirst();
    }

    public Long save(CreateUserRequest req, String passwordHash) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO users(full_name, email, password_hash, phone) VALUES(?,?,?,?)",
                    new String[]{"user_id"});
            ps.setString(1, req.fullName());
            ps.setString(2, req.email());
            ps.setString(3, passwordHash);
            ps.setString(4, req.phone());
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public int update(Long id, UpdateUserRequest req) {
        return jdbc.update(
                "UPDATE users SET full_name=?, email=?, phone=?, is_active=? WHERE user_id=?",
                req.fullName(), req.email(), req.phone(), req.isActive(), id);
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM users WHERE user_id=?", id);
    }
}
