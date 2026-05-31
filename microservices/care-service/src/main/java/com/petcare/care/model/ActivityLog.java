package com.petcare.care.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "activity_logs")
public class ActivityLog {

    @Id
    private String id;
    private Long petId;
    private String activityType;
    private double durationMinutes;
    private LocalDateTime loggedAt;
    private String notes;

    public ActivityLog() {}

    public ActivityLog(Long petId, String activityType, double durationMinutes, LocalDateTime loggedAt, String notes) {
        this.petId = petId;
        this.activityType = activityType;
        this.durationMinutes = durationMinutes;
        this.loggedAt = loggedAt;
        this.notes = notes;
    }

    public String getId() { return id; }
    public Long getPetId() { return petId; }
    public String getActivityType() { return activityType; }
    public double getDurationMinutes() { return durationMinutes; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public String getNotes() { return notes; }

    public void setId(String id) { this.id = id; }
    public void setPetId(Long petId) { this.petId = petId; }
    public void setActivityType(String a) { this.activityType = a; }
    public void setDurationMinutes(double d) { this.durationMinutes = d; }
    public void setLoggedAt(LocalDateTime l) { this.loggedAt = l; }
    public void setNotes(String n) { this.notes = n; }
}
