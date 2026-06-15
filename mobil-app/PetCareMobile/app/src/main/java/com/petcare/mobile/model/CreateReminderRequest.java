package com.petcare.mobile.model;

public class CreateReminderRequest {
    private final Long petId;
    private final String reminderType;
    private final String title;
    private final String remindAt;
    private final String status;

    public CreateReminderRequest(Long petId, String reminderType, String title, String remindAt, String status) {
        this.petId = petId;
        this.reminderType = reminderType;
        this.title = title;
        this.remindAt = remindAt;
        this.status = status;
    }
}
