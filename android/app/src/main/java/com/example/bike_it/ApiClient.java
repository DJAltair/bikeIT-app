package com.example.bike_it;

import android.content.SharedPreferences;

import com.example.bike_it.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.0.166:8080";
    private static SharedPreferences sharedPreferences;
    private static Retrofit retrofit;

    public static void createRetrofitInstance(SharedPreferences varSharedPreferences) {
        sharedPreferences = varSharedPreferences;

        Retrofit tokenRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = tokenRetrofit.create(ApiService.class);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(sharedPreferences))
                .authenticator(new TokenAuthenticator(apiService, sharedPreferences))
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            throw new IllegalStateException("Retrofit instance not created");
        }
        return retrofit;
    }
}