package com.example.bike_it;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.bike_it.requests.LoginRequest;
import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.responses.ApiNotification;
import com.example.bike_it.responses.ApiUserIDResponse;
import com.example.bike_it.responses.LoginResponse;
import com.example.bike_it.responses.RefreshResponse;
import com.example.bike_it.ui.profile.ProfileActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "example_channel_id";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;

    // Note: BuildConfig.TOMTOM_API_KEY could potentially be null, ensure it's not null before use.
    // TomTom APIs will not work without a valid API key. Navigation SDK is only avaialble upon request.
    // Use the API key provided by TomTom to start using the SDK.
    private TomTomMap tomtomMap;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    static Boolean once = false;

    private Handler handler = new Handler();
    private Runnable runnable;
    private final int interval = 5000; // 1 second

    static int lastGlobalNotificationId = 0;
    static List<String> lastFriendRequestNotification = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // updateTheme();
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        {
            SharedPreferences pref = getSharedPreferences("user_prefs", MODE_PRIVATE);
            ApiClient.createRetrofitInstance(pref, ApiClient.getSecureOkHttpClient(this, pref));
        }

        tryRefreshToken();

        if (!isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_activity, R.id.nav_home, R.id.nav_gallery, R.id.nav_maps, R.id.nav_slideshow)
                 .setOpenableLayout(drawer)
                 .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(isLoggedIn())
        {
            updateProfileInformation();
        }

        updateTheme();

        String lang = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("language", "english");


        if(lang != "english" && once == false)
        {
            once = true;
            updateLanguage();
        }

        // Notifications

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
        }

        createNotificationChannel();

        runnable = new Runnable() {
            @Override
            public void run() {
                // Your periodic task
                doPeriodicTask();

                // Schedule the runnable to run again after the interval
                handler.postDelayed(this, interval);
            }
        };

        // Start the periodic task
        handler.post(runnable);
    }

    private void createNotificationChannel() {
        CharSequence name = "Example Channel";
        String description = "Channel for example notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void sendNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_friends)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void doPeriodicTask()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        {
            Call<ApiNotification> call = apiService.getNotifications();

            call.enqueue(new Callback<ApiNotification>() {
                @Override
                public void onResponse(Call<ApiNotification> call, Response<ApiNotification> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if(response.body().id != lastGlobalNotificationId)
                        {
                            lastGlobalNotificationId = response.body().id;
                            Log.d("Notification", response.body().content);
                            sendNotification("Bike-it notification!", response.body().content);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiNotification> call, Throwable t) {
                    Log.d("Notification", "onFailure: " + t.getMessage());
                }
            });
        }

        {
            Call<List<ApiFriendshipsUsers>> call = apiService.getFriendRequests();
            call.enqueue(new Callback<List<ApiFriendshipsUsers>>() {
                @Override
                public void onResponse(Call<List<ApiFriendshipsUsers>> call, Response<List<ApiFriendshipsUsers>> response) {
                    if(response.body() == null)
                        return;

                    List<String> strings = new ArrayList<>();

                    for(ApiFriendshipsUsers user : response.body())
                    {
                        strings.add(user.getUsername());
                    }

                    strings.sort(String.CASE_INSENSITIVE_ORDER);
                    List<String> diff = new ArrayList<String>(strings);

                    for(String s : lastFriendRequestNotification)
                    {
                        diff.removeIf(x -> x.equals(s));
                    }

                    if(!diff.isEmpty() || lastFriendRequestNotification.size() > strings.size())
                        lastFriendRequestNotification = strings;

                    if(!diff.isEmpty())
                    {
                        lastFriendRequestNotification = strings;
                        String username = diff.get(0);
                        Log.d("Notification", username);
                        sendNotification("New friend request", "User " + username + " has sent you a friend request");
                    }
                }

                @Override
                public void onFailure(Call<List<ApiFriendshipsUsers>> call, Throwable t) {
                    Log.d("FriendsFragment", "onFailure: " + t.getMessage());

                }
            });
        }



        Log.d("Notification", "Dupa");
    }

    private void updateProfileInformation()
    {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);

        // Get the TextViews from the header layout
        TextView navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        TextView navHeaderSubtitle = headerView.findViewById(R.id.nav_header_subtitle);

        String user_id = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("username", null);

        if(user_id == null)
        {
            navHeaderTitle.setText("Anonymous");
            navHeaderSubtitle.setText("Anonymous");
            return;
        }

        // Set the name and email
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ApiUserIDResponse> call = apiService.userProfile(user_id);
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

    public void showCurrentProfile(View view)
    {
        Intent i = new Intent(this, ProfileActivity.class);

        String username = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("username", null);

        if(username == null)
            return;

        i.putExtra("profile_username", username);
        startActivity(i);
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
        }
        else if (item.getItemId() == R.id.action_switch_theme) {
            Boolean mode = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getBoolean("darkModeEnabled", false);

            mode = !mode;

            getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                    .putBoolean("darkModeEnabled", mode)
                    .apply();

            updateTheme();

            return true;
        }
        else if (item.getItemId() == R.id.action_switch_language) {
            String lang = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .getString("language", "english");

            if(lang.equals("english"))
            {
                lang = "russian";
            }
            else
            {
                lang = "english";
            }

            getSharedPreferences("user_prefs", MODE_PRIVATE).edit()
                    .putString("language", lang)
                    .apply();

            updateLanguage();

            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    void updateLanguage()
    {
        String lang = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("language", "english");

        if(lang == "english")
            lang = "en";
        else
            lang = "ru";

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);

        // Restart activity to apply changes
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    void updateTheme()
    {
        Boolean mode = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getBoolean("darkModeEnabled", false);

        if(mode)
        {
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_NO)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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