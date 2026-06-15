package com.petcare.mobile.model;

public class UserResponse {

    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private boolean isActive;
    private String createdAt;
    private String userRole;   // "USER" veya "VET"

    public Long getUserId()     { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public boolean isActive()   { return isActive; }
    public String getCreatedAt(){ return createdAt; }
    public String getUserRole() { return userRole; }
}
