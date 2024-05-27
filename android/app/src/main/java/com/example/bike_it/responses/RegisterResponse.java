package com.example.bike_it.responses;

public class RegisterResponse {
    private boolean success;
    private int id;
    private String name;
    private String username;
    private String description;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getName() { return this.name; }
    public String getUsername () { return this.username; }
    public String getDescription () { return this.description; }
}
