package com.example.bike_it;

import com.example.bike_it.requests.CreatePostRequest;
import com.example.bike_it.requests.LoginRequest;
import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.responses.ApiMap;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.responses.ApiUserIDResponse;
import com.example.bike_it.responses.LoginResponse;
import com.example.bike_it.responses.RefreshResponse;
import com.example.bike_it.requests.RegisterRequest;
import com.example.bike_it.responses.RegisterResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    // Posty

    @GET("api/posts")
    Call<List<ApiPostsResponse>> getPosts();

    @GET("api/posts/{username}")
    Call<List<ApiPostsResponse>> getPostsUsername(@Path("username") String username);

    @GET("api/post/{id}")
    Call<ApiPostsResponse> getPost(@Path("id") int id);

    @DELETE("api/post/{id}")
    Call<ResponseBody> deletePost(@Path("id") int id);

    @POST("api/post")
    Call<ApiPostsResponse> createPost(@Body CreatePostRequest post);

    // Friends

    @GET("api/friendships/friends")
    Call<List<ApiFriendshipsUsers>> getFriends();

    // MAPS

    @GET("api/map/all")
    Call<List<ApiMap>> getAllMaps();

    @DELETE("api/map/{id}")
    Call<ResponseBody> deleteMap(@Path("id") int id);

    @GET("api/map/{id}")
    Call<ApiMap> getMap(@Path("id") int id);
}