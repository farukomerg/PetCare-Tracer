package com.petcare.mobile.model;

public class LoginResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private Boolean isActive;
    private String createdAt;

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
