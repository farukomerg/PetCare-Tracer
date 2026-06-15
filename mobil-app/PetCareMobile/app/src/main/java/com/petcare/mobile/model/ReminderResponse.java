package com.petcare.mobile.model;

public class ReminderResponse {
    private Long reminderId;
    private Long petId;
    private String reminderType;
    private String title;
    private String remindAt;
    private String status;
    private String createdAt;

    public Long getReminderId() { return reminderId; }
    public Long getPetId() { return petId; }
    public String getReminderType() { return reminderType; }
    public String getTitle() { return title; }
    public String getRemindAt() { return remindAt; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
