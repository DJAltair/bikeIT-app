package com.example.bike_it.responses;

public class LoginResponse {
    private boolean success;
    private String access_token;
    private String refresh_token;

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String token) {
        this.access_token = token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String token) {
        this.refresh_token = token;
    }
}