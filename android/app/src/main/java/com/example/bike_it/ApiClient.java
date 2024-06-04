package com.example.bike_it;

import android.content.SharedPreferences;
import android.content.Context;

import com.example.bike_it.AuthInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ApiClient {
    private static final String BASE_URL = "https://bikeit.redskittlefox.com";
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

    public static OkHttpClient getSecureOkHttpClient(Context context) {
        try {
            Retrofit tokenRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = tokenRetrofit.create(ApiService.class);

            // Load the BKS keystore
            KeyStore keyStore = KeyStore.getInstance("BKS");
            // Ensure the keystore.bks file is in the res/raw directory
            InputStream in = context.getResources().openRawResource(R.raw.keystore); // keystore.bks in res/raw
            keyStore.load(in, "ZfkV98jAVEiZeLaWmmAE".toCharArray()); // "keystore_password" is the password used when creating the BKS keystore

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            X509TrustManager trustManager = (X509TrustManager) tmf.getTrustManagers()[0];

            // Create an SSLContext that uses our TrustManager
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.ca_bundle);
            Certificate ca;
            try {
                ca = cf.generateCertificate(caInput);
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CA

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new javax.net.ssl.TrustManager[]{trustManager}, null);

            // Create and return an OkHttpClient that uses the SSLContext
            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
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