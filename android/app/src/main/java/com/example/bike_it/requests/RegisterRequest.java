package com.example.bike_it.requests;

public class RegisterRequest {
    private String name;
    private String username;
    private String password;

    public RegisterRequest(String username, String password, String email) {
        this.name = email;
        this.username = username;
        this.password = password;
    }
}
