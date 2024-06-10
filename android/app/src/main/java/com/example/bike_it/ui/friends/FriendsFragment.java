package com.example.bike_it.ui.friends;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.ui.post.Post;
import com.example.bike_it.ui.post.PostAdapter;
import com.example.bike_it.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendsFragment extends Fragment {

    private List<ApiFriendshipsUsers> pendingFriends;
    private List<ApiFriendshipsUsers> friends;


    private ListView listViewFriends;
    private ListView listViewFriendRequests;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        listViewFriends = view.findViewById(R.id.listViewFriends);
        listViewFriendRequests = view.findViewById(R.id.listViewFriendRequests);

        Button buttonFindFriend = view.findViewById(R.id.buttonFindFriend);
        buttonFindFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(container.getContext(), FindFirendActivity.class);
                container.getContext().startActivity(i);
            }
        });


        // Create list of friends
        List<ApiFriendshipsUsers> friends = new ArrayList<>();
        friends.add(new ApiFriendshipsUsers());

        pendingFriends = new ArrayList<>();
        pendingFriends.add(new ApiFriendshipsUsers("djaltair", "djaltair"));

        fetchRequests();
        fetchFriends();

        return view;
    }

    void updateFriendsViews()
    {
        friends.forEach( x -> x.status = ApiFriendshipsUsers.ApiFriendshipsStatus.friend );

        FriendListAdapter adapter = new FriendListAdapter(requireContext(), R.layout.item_friend, friends);
        listViewFriends.setAdapter(adapter);
    }

    void updateRequestsViews()
    {
        pendingFriends.forEach( x -> x.status = ApiFriendshipsUsers.ApiFriendshipsStatus.requested );

        FriendListAdapter adapter = new FriendListAdapter(requireContext(), R.layout.item_friend, pendingFriends);
        listViewFriendRequests.setAdapter(adapter);
    }

    void fetchRequests()
    {
        updateRequestsViews();
    }


    void fetchFriends()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<List<ApiFriendshipsUsers>> call = apiService.getFriends();

        call.enqueue(new Callback<List<ApiFriendshipsUsers>>() {
            @Override
            public void onResponse(Call<List<ApiFriendshipsUsers>> call, Response<List<ApiFriendshipsUsers>> response) {
                friends = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    friends = response.body();
                } else {

                }

                friends.add(new ApiFriendshipsUsers());

                updateFriendsViews();
            }

            @Override
            public void onFailure(Call<List<ApiFriendshipsUsers>> call, Throwable t) {
                // Handle network failure
                // Toast.makeText(PostListFragment.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.d("LoginActivity", "onFailure: " + t.getMessage());

            }
        });
    }
}