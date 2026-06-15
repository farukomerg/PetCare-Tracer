package com.petcare.care.repository;

import com.petcare.care.dto.CreateReminderRequest;
import com.petcare.care.model.Reminder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class ReminderRepository {

    private final JdbcTemplate jdbc;

    public ReminderRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private Reminder mapRow(ResultSet rs, int rn) throws SQLException {
        return new Reminder(
                rs.getLong("reminder_id"), rs.getLong("pet_id"),
                rs.getString("reminder_type"), rs.getString("title"),
                rs.getTimestamp("remind_at").toLocalDateTime(),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime());
    }

    public List<Reminder> findAll() {
        return jdbc.query("SELECT * FROM reminders ORDER BY remind_at DESC", this::mapRow);
    }

    public List<Reminder> findByPetId(Long petId) {
        return jdbc.query("SELECT * FROM reminders WHERE pet_id=? ORDER BY remind_at", this::mapRow, petId);
    }

    public Optional<Reminder> findById(Long id) {
        return jdbc.query("SELECT * FROM reminders WHERE reminder_id=?", this::mapRow, id).stream().findFirst();
    }

    public Long save(CreateReminderRequest r) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO reminders(pet_id,reminder_type,title,remind_at,status) VALUES(?,?,?,?,?)",
                    new String[]{"reminder_id"});
            ps.setLong(1, r.petId());
            ps.setString(2, r.reminderType());
            ps.setString(3, r.title());
            ps.setTimestamp(4, Timestamp.valueOf(r.remindAt()));
            ps.setString(5, r.status() != null ? r.status() : "PENDING");
            return ps;
        }, kh);
        return kh.getKey().longValue();
    }

    public int deleteById(Long id) {
        return jdbc.update("DELETE FROM reminders WHERE reminder_id=?", id);
    }
}
