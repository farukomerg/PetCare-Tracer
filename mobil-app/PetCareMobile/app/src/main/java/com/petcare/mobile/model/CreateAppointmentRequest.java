package com.petcare.mobile.model;

public class CreateAppointmentRequest {
    private final Long petId;
    private final String vetName;
    private final String clinicName;
    private final String appointmentTime;
    private final String status;
    private final String note;

    public CreateAppointmentRequest(Long petId, String vetName, String clinicName,
                                    String appointmentTime, String status, String note) {
        this.petId = petId;
        this.vetName = vetName;
        this.clinicName = clinicName;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.note = note;
    }
}
