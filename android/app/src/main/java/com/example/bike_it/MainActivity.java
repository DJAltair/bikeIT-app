package com.example.bike_it;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.bike_it.requests.LoginRequest;
import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.ApiUserIDResponse;
import com.example.bike_it.responses.LoginResponse;
import com.example.bike_it.responses.RefreshResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;

import com.example.bike_it.databinding.ActivityMainBinding;

import com.tomtom.sdk.map.display.ui.MapFragment;
import com.tomtom.sdk.map.display.TomTomMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    // Note: BuildConfig.TOMTOM_API_KEY could potentially be null, ensure it's not null before use.
    // TomTom APIs will not work without a valid API key. Navigation SDK is only avaialble upon request.
    // Use the API key provided by TomTom to start using the SDK.
    private TomTomMap tomtomMap;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiClient.createRetrofitInstance(getSharedPreferences("user_prefs", MODE_PRIVATE));

        tryRefreshToken();

        if (!isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_activity, R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(isLoggedIn())
        {
            updateProfileInformation();
        }
    }

    private void updateProfileInformation()
    {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        // Get the TextViews from the header layout
        TextView navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        TextView navHeaderSubtitle = headerView.findViewById(R.id.nav_header_subtitle);

        int user_id = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getInt("user_id", -1);

        if(user_id == -1)
        {
            navHeaderTitle.setText("Anonymous");
            navHeaderSubtitle.setText("Anonymous");
            return;
        }

        // Set the name and email
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        RefreshRequest refreshRequest = new RefreshRequest();

        Call<ApiUserIDResponse> call = apiService.userProfile(Integer.toString(user_id));
        call.enqueue(new Callback<ApiUserIDResponse>() {
            @Override
            public void onResponse(Call<ApiUserIDResponse> call, Response<ApiUserIDResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful login
                    navHeaderTitle.setText(response.body().name);
                    navHeaderSubtitle.setText(response.body().username);
                } else {
                    navHeaderTitle.setText("<network error>");
                    navHeaderSubtitle.setText("<network error>");
                }
            }

            @Override
            public void onFailure(Call<ApiUserIDResponse> call, Throwable t) {
                // Handle network failure
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "onFailure: " + t.getMessage());

                logout();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_logout) {
            // Perform logout action here
            logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private boolean isLoggedIn() {
        return getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getBoolean("logged_in", false);
    }

    private void tryRefreshToken() {
        if(!isLoggedIn())
            return;

        String refresh_token = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("refresh_token", null);

        if(refresh_token == null)
        {
            getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("logged_in", false)
                .putString("access_token", null)
                .putString("refresh_token", null)
                .apply();

            return;
        }

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        RefreshRequest refreshRequest = new RefreshRequest();

        Call<RefreshResponse> call = apiService.refreshToken(refreshRequest);
        call.enqueue(new Callback<RefreshResponse>() {
            @Override
            public void onResponse(Call<RefreshResponse> call, Response<RefreshResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful login
                    getSharedPreferences("user_prefs", MODE_PRIVATE)
                            .edit()
                            .putBoolean("logged_in", true)
                            .putString("access_token", response.body().getAccessToken())
                            .apply();

                } else {
                    logout();
                }
            }

            @Override
            public void onFailure(Call<RefreshResponse> call, Throwable t) {
                // Handle network failure
                Toast.makeText(MainActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "onFailure: " + t.getMessage());

                logout();
            }
        });
    }

    private void logout() {
        // Clear tokens and navigate to login screen
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("logged_in", false)
                .putString("access_token", null)
                .putString("refresh_token", null)
                .putInt("user_id", -1)
                .apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}