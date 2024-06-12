package com.example.bike_it;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bike_it.AuthInterceptor;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://192.168.0.25:8443";
    private static SharedPreferences sharedPreferences;
    private static Retrofit retrofit;

    public static void createRetrofitInstance(SharedPreferences varSharedPreferences, OkHttpClient okHttpClient) {
        sharedPreferences = varSharedPreferences;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public static OkHttpClient getSecureOkHttpClient(Context context, SharedPreferences varSharedPreferences) {
        try {
            sharedPreferences = varSharedPreferences;

            Retrofit tokenRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = tokenRetrofit.create(ApiService.class);

            InputStream keyStoreInputStream = context.getResources().openRawResource(R.raw.client_identity);
            InputStream trustStoreInputStream = context.getResources().openRawResource(R.raw.client_truststore);

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(keyStoreInputStream, "secret".toCharArray());

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(trustStoreInputStream, "secret".toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, "secret".toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());


            // Create and return an OkHttpClient that uses the SSLContext
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                    .addInterceptor(new AuthInterceptor(sharedPreferences))
                    .authenticator(new TokenAuthenticator(apiService, sharedPreferences))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            throw new IllegalStateException("Retrofit instance not created");
        }
        return retrofit;
    }
}