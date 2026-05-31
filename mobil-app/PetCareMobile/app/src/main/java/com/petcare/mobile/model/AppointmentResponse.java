package com.petcare.mobile.model;

public class AppointmentResponse {
    private Long appointmentId;
    private Long petId;
    private String vetName;
    private String clinicName;
    private String appointmentTime;
    private String status;
    private String note;

    public Long getAppointmentId() { return appointmentId; }
    public Long getPetId() { return petId; }
    public String getVetName() { return vetName; }
    public String getClinicName() { return clinicName; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getNote() { return note; }
}
