package com.example.bike_it.responses;

public class RefreshResponse {
    private boolean success;
    private String access_token;

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
}
