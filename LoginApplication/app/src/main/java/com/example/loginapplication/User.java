package com.example.loginapplication;

public class User {
    private String name;
    private String email;
    private String mobile;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String mobile) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    // Getters and setters for name, email, and mobile (optional)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}

