package com.petcare.mobile.model;

public class RegisterRequest {

    private final String fullName;
    private final String email;
    private final String password;
    private final String phone;
    private final String userRole;   // "USER" veya "VET"

    public RegisterRequest(String fullName, String email, String password,
                           String phone, String userRole) {
        this.fullName = fullName;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.userRole = userRole;
    }

    public String getFullName()  { return fullName; }
    public String getEmail()     { return email; }
    public String getPassword()  { return password; }
    public String getPhone()     { return phone; }
    public String getUserRole()  { return userRole; }
}
