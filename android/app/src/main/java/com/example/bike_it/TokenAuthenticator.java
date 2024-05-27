package com.example.bike_it;

import android.content.SharedPreferences;

import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.RefreshResponse;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {
    private SharedPreferences sharedPreferences;
    private ApiService apiService;

    public TokenAuthenticator( ApiService apiService, SharedPreferences varSharedPreferences)
    {
        this.sharedPreferences = varSharedPreferences;
        this.apiService = apiService;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Synchronize to avoid refreshing token multiple times
        synchronized (this) {
            String refresh_token = sharedPreferences
                    .getString("refresh_token", null);

            if(refresh_token == null)
            {
                return null;
            }

            Call<RefreshResponse> call = apiService.refreshToken(new RefreshRequest());
            retrofit2.Response<RefreshResponse> refreshResponse = call.execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                // Update the tokens in the TokenManager
                String newAccessToken = refreshResponse.body().getAccessToken();

                sharedPreferences
                        .edit()
                        .putBoolean("logged_in", true)
                        .putString("access_token", newAccessToken)
                        .apply();

                // Retry the original request with the new access token
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            } else {
                // Refresh token failed, clear tokens and give up
                sharedPreferences
                        .edit()
                        .putBoolean("logged_in", false)
                        .putString("access_token", null)
                        .putString("refresh_token", null)
                        .apply();

                return null;
            }

        }
    }
}
