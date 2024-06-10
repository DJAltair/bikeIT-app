package com.example.bike_it.ui.post;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity {

    private int postId;
    private Post post;

    Context context;
    TextView textViewTitle;
    TextView textViewText;
    TextView textViewDate;
    TextView textViewAuthor;

    ImageView postImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post);
        /* ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("Post");

        textViewTitle = findViewById(R.id.textViewTitle);
        textViewText = findViewById(R.id.textViewText);
        textViewDate = findViewById(R.id.textViewDate);
        textViewAuthor = findViewById(R.id.textViewAuthor);
        postImageView = findViewById(R.id.postImageView);

        {
            Intent i = getIntent();
            postId = i.getIntExtra("post_id", -1);

            if(postId == -1)
                return;

            fetchPost();
        }
    }

    private void fetchPost()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ApiPostsResponse> call = apiService.getPost(postId);

        call.enqueue(new Callback<ApiPostsResponse>() {
            @Override
            public void onResponse(Call<ApiPostsResponse> call, Response<ApiPostsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    post = new Post(response.body());
                    updatePost();
                } else {
                    post = null;
                }
            }

            @Override
            public void onFailure(Call<ApiPostsResponse> call, Throwable t) {
                Log.d("LoginActivity", "onFailure: " + t.getMessage());

            }
        });
    }

    private void updatePost()
    {
        textViewTitle.setText(post.getTitle());
        textViewText.setText(post.getText());
        textViewDate.setText(post.getDate());
        textViewAuthor.setText(post.getAuthor());

        if(post.getImage() != null)
        {
            postImageView.setImageBitmap(post.getImage());
        }
    }

    public void onUsernameClick(View v)
    {
        Intent i = new Intent(this, ProfileActivity.class);

        if(post == null)
            return;

        if(post.getAuthor() == null)
            return;

        i.putExtra("profile_username", post.getAuthor());
        startActivity(i);
    }

    public void onDeletePostClick(View v)
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.deletePost(postId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent i = new Intent(context, ProfileActivity.class);

                if(post.getAuthor() == null)
                    return;

                i.putExtra("profile_username", post.getAuthor());
                startActivity(i);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("LoginActivity", "onFailure: " + t.getMessage());

            }
        });
    }
}