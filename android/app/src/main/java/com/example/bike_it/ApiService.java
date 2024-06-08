package com.example.bike_it;

import com.example.bike_it.requests.LoginRequest;
import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.responses.ApiUserIDResponse;
import com.example.bike_it.responses.LoginResponse;
import com.example.bike_it.responses.RefreshResponse;
import com.example.bike_it.requests.RegisterRequest;
import com.example.bike_it.responses.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Path;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("auth/login")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest registerRequest);

    @POST("auth/refresh")
    Call<RefreshResponse> refreshToken(@Body RefreshRequest refreshRequest);

    @GET("api/profile/{username}")
    Call<ApiUserIDResponse> userProfile(@Path("username") String username);

    @GET("api/posts")
    Call<List<ApiPostsResponse>> getPosts();

    @GET("api/posts/{username}")
    Call<List<ApiPostsResponse>> getPostsUsername(@Path("username") String username);
}