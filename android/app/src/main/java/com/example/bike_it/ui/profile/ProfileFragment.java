package com.example.bike_it.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.databinding.FragmentSlideshowBinding;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.ui.post.Post;
import com.example.bike_it.ui.post.PostAdapter;
import com.example.bike_it.ui.post.PostListFragment;
import com.example.bike_it.ui.slideshow.SlideshowViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private List<Post> postList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updatePostList();

        return view;
    }

    public void updatePostList()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<List<ApiPostsResponse>> call = apiService.getPosts();

        call.enqueue(new Callback<List<ApiPostsResponse>>() {
            @Override
            public void onResponse(Call<List<ApiPostsResponse>> call, Response<List<ApiPostsResponse>> response) {
                postList = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    // Handle successful login
                    for (ApiPostsResponse r : response.body()) {
                        postList.add(new Post(r.getContent(), r.getCreatedAt(), r.getUsername()));
                    }

                } else {
                    postList.add(new Post("<Failed to load posts>", "<Networking Error>", "<Networking Error>"));
                }

                adapter = new PostAdapter(postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<ApiPostsResponse>> call, Throwable t) {
                // Handle network failure
                // Toast.makeText(PostListFragment.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", "onFailure: " + t.getMessage());

            }
        });
    }
}