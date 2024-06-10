package com.example.bike_it.ui.maps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.LoginActivity;
import com.example.bike_it.MainActivity;
import com.example.bike_it.R;
import com.example.bike_it.requests.CreatePostRequest;
import com.example.bike_it.responses.ApiMap;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.ui.post.PostActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShareMapActivity extends AppCompatActivity {

    int mapId;
    ApiMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_share_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mapId = getIntent().getIntExtra("map_id", -1);
        if(mapId == -1)
        {
            doCancel();
        }

        findViewById(R.id.sharePostButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postPost();
                    }
                }
        );

        findViewById(R.id.cancelPostButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCancel();
                    }
                }
        );

        fetchMap();
    }

    void fetchMap()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiMap> call = apiService.getMap(mapId);
        call.enqueue(new Callback<ApiMap>() {
            @Override
            public void onResponse(Call<ApiMap> call, Response<ApiMap> response) {
                if (response.isSuccessful() && response.body() != null) {
                    map = response.body();
                } else {
                    map = null;
                }

                updateMap();
            }

            @Override
            public void onFailure(Call<ApiMap> call, Throwable t) {
                Log.d("MapFragment", "onFailure: " + t.getMessage());
            }
        });
    }

    void updateMap()
    {
        if(map.getImage() != null)
        {
            ImageView imageview = findViewById(R.id.imageViewMap);
            imageview.setImageBitmap(map.getImage());
        }
    }

    void postPost()
    {
        String title = ((EditText)findViewById(R.id.editTextPostTitle)).getText().toString();
        String desc = ((EditText)findViewById(R.id.editTextPostDescription)).getText().toString();

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiPostsResponse> call = apiService.createPost(new CreatePostRequest(title, desc, map.getImageBase64()));
        call.enqueue(new Callback<ApiPostsResponse>() {
            @Override
            public void onResponse(Call<ApiPostsResponse> call, Response<ApiPostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    doOpenPost(response.body().getId());
                } else {
                    doCancel();
                }
            }

            @Override
            public void onFailure(Call<ApiPostsResponse> call, Throwable t) {
                Log.d("MapFragment", "onFailure: " + t.getMessage());
            }
        });
    }

    public void doCancel()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void doOpenPost(int postId)
    {
        Intent i = new Intent(this, PostActivity.class);
        i.putExtra("post_id", postId);
        startActivity(i);
    }
}