package com.example.bike_it.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.MainActivity;
import com.example.bike_it.R;
import com.example.bike_it.requests.RefreshRequest;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.responses.ApiUserIDResponse;
import com.example.bike_it.ui.post.Post;
import com.example.bike_it.ui.post.PostAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList;

    private String profileName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_profile);

        /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Profile"); // Set the title for the toolbar

        profileName = getIntent().getStringExtra("profile_username");

        if(profileName != null)
        {
            fetchUserInformation();

            //  View view = inflater.inflate(R.layout.fragment_post_list, container, false);
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            updatePostList();
        }
    }

    void fetchUserInformation()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiUserIDResponse> call = apiService.userProfile(this.profileName);

        call.enqueue(new Callback<ApiUserIDResponse>() {
            @Override
            public void onResponse(Call<ApiUserIDResponse> call, Response<ApiUserIDResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TextView profileNameView = (TextView)findViewById(R.id.profile_name);
                    TextView profileDescView = (TextView)findViewById(R.id.profile_desc);
                    TextView profileFullNameView = (TextView)findViewById(R.id.profile_real_name);

                    profileNameView.setText(response.body().username);
                    profileDescView.setText(response.body().description);
                    profileFullNameView.setText(response.body().name);

                } else {
                    TextView profileNameView = (TextView)findViewById(R.id.profile_name);
                    TextView profileDescView = (TextView)findViewById(R.id.profile_desc);
                    TextView profileFullNameView = (TextView)findViewById(R.id.profile_real_name);

                    profileNameView.setText("<network error>");
                    profileDescView.setText("<network error>");
                    profileFullNameView.setText("<network error>");
                }
            }

            @Override
            public void onFailure(Call<ApiUserIDResponse> call, Throwable t) {
                // Handle network failure
                Toast.makeText(ProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("ProfileActivity", "onFailure: " + t.getMessage());
            }
        });
    }

    public void updatePostList()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<List<ApiPostsResponse>> call = apiService.getPostsUsername(profileName);

        call.enqueue(new Callback<List<ApiPostsResponse>>() {
            @Override
            public void onResponse(Call<List<ApiPostsResponse>> call, Response<List<ApiPostsResponse>> response) {
                postList = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful login
                    for (ApiPostsResponse r : response.body()) {
                        postList.add(new Post(r));
                    }

                } else {
                    postList.add(new Post("<Failed to load posts>", "<Failed to load posts>", "<Networking Error>", "<Networking Error>", null, 0));
                }

                adapter = new PostAdapter(postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ApiPostsResponse>> call, Throwable t) {
                // Handle network failure
                // Toast.makeText(PostListFragment.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity", "onFailure: " + t.getMessage());

            }
        });
    }
}