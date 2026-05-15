package com.petcarebackend.repository;

import com.petcarebackend.dto.reminder.CreateReminderRequest;
import com.petcarebackend.model.Reminder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ReminderRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReminderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Reminder> findAll() {
        String sql = """
                SELECT reminder_id, pet_id, reminder_type, title, remind_at, status, created_at
                FROM reminders
                ORDER BY reminder_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Reminder(
                rs.getLong("reminder_id"),
                rs.getLong("pet_id"),
                rs.getString("reminder_type"),
                rs.getString("title"),
                rs.getTimestamp("remind_at").toLocalDateTime(),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ));
    }

    public Optional<Reminder> findById(Long reminderId) {
        String sql = """
                SELECT reminder_id, pet_id, reminder_type, title, remind_at, status, created_at
                FROM reminders
                WHERE reminder_id = ?
                """;

        List<Reminder> results = jdbcTemplate.query(sql, (rs, rowNum) -> new Reminder(
                rs.getLong("reminder_id"),
                rs.getLong("pet_id"),
                rs.getString("reminder_type"),
                rs.getString("title"),
                rs.getTimestamp("remind_at").toLocalDateTime(),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), reminderId);

        return results.stream().findFirst();
    }

    public List<Reminder> findByPetId(Long petId) {
        String sql = """
                SELECT reminder_id, pet_id, reminder_type, title, remind_at, status, created_at
                FROM reminders
                WHERE pet_id = ?
                ORDER BY reminder_id
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Reminder(
                rs.getLong("reminder_id"),
                rs.getLong("pet_id"),
                rs.getString("reminder_type"),
                rs.getString("title"),
                rs.getTimestamp("remind_at").toLocalDateTime(),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime()
        ), petId);
    }

    public Long save(CreateReminderRequest request) {
        String sql = """
                INSERT INTO reminders (pet_id, reminder_type, title, remind_at, status)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"reminder_id"});
            ps.setLong(1, request.petId());
            ps.setString(2, request.reminderType());
            ps.setString(3, request.title());
            ps.setTimestamp(4, Timestamp.valueOf(request.remindAt()));
            ps.setString(5, request.status());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public int update(Long reminderId, CreateReminderRequest request) {
        String sql = """
                UPDATE reminders
                SET pet_id = ?, reminder_type = ?, title = ?, remind_at = ?, status = ?
                WHERE reminder_id = ?
                """;

        return jdbcTemplate.update(sql,
                request.petId(),
                request.reminderType(),
                request.title(),
                Timestamp.valueOf(request.remindAt()),
                request.status(),
                reminderId
        );
    }

    public int deleteById(Long reminderId) {
        return jdbcTemplate.update("DELETE FROM reminders WHERE reminder_id = ?", reminderId);
    }
}
