package com.example.bike_it;

import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class AuthInterceptor implements Interceptor {
    private static SharedPreferences sharedPreferences;

    public AuthInterceptor(SharedPreferences varSharedPreferences)
    {
        sharedPreferences = varSharedPreferences;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        String og_url = original.url().toString();

        String token = null;
        if(og_url.endsWith("auth/login") || original.url().toString().endsWith("auth/register"))
        {
            token = null;
        }
        else if(og_url.endsWith("auth/refresh"))
        {
            if(sharedPreferences.getBoolean("logged_in", false))
            {
                token = sharedPreferences.getString("refresh_token", null);
            }
        }
        else
        {
            if(sharedPreferences.getBoolean("logged_in", false))
            {
                token = sharedPreferences.getString("access_token", null);
            }
        }

        if (token != null) {
            Request.Builder builder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Request request = builder.build();
            return chain.proceed(request);
        } else {
            return chain.proceed(original);
        }
    }
}
