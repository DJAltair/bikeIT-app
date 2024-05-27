package com.example.bike_it;

import com.example.bike_it.requests.LoginRequest;
import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.LoginResponse;
import com.example.bike_it.responses.RefreshResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("auth/refresh")
    Call<RefreshResponse> refreshToken(@Body RefreshRequest refreshRequest);
}