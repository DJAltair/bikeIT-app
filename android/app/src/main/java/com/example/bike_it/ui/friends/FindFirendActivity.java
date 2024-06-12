package com.example.bike_it.ui.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.responses.ApiFriendshipsUsers;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindFirendActivity extends AppCompatActivity {

    private EditText editTextUserSearch;
    private ListView listViewUsers;
    private List<ApiFriendshipsUsers> users;

    private List<ApiFriendshipsUsers> pendingFriends;
    private List<ApiFriendshipsUsers> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_find_firend);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listViewUsers = findViewById(R.id.listViewUsers);
        editTextUserSearch = findViewById(R.id.editTextUserSearch);

        editTextUserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateUsers();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed
            }
        });

        fetchRequests();
    }

    public void fetchUsers()
    {
        users = new ArrayList<>();
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<ApiFriendshipsUsers>> call = apiService.getUsers();
        call.enqueue(new Callback<List<ApiFriendshipsUsers>>() {
            @Override
            public void onResponse(Call<List<ApiFriendshipsUsers>> call, Response<List<ApiFriendshipsUsers>> response) {
                pendingFriends = new ArrayList<>();

                users = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    users = response.body();
                } else {

                }

                updateUsers();
            }

            @Override
            public void onFailure(Call<List<ApiFriendshipsUsers>> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }

    public void updateUsers()
    {
        List<ApiFriendshipsUsers> filteredUsers = new ArrayList<>(users);
        String t = editTextUserSearch.getText().toString();
        if(t != "")
        {
            filteredUsers.removeIf(x -> !x.getUsername().startsWith(t));
        }

        String user_id = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("username", null);

        filteredUsers.removeIf(x -> { return x.getUsername().equals(user_id); });

        for(ApiFriendshipsUsers f : friends)
        {
            filteredUsers.removeIf(x -> {
                return x.getUsername().equals(f.getUsername());
            });
        }

        for(ApiFriendshipsUsers f : pendingFriends)
        {
            filteredUsers.removeIf(x -> {
                return x.getUsername().equals(f.getUsername());
            });
        }

        FriendListAdapter adapter = new FriendListAdapter(this, R.layout.item_friend, filteredUsers);
        listViewUsers.setAdapter(adapter);
    }

    void fetchRequests()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<List<ApiFriendshipsUsers>> call = apiService.getFriendRequests();
        call.enqueue(new Callback<List<ApiFriendshipsUsers>>() {
            @Override
            public void onResponse(Call<List<ApiFriendshipsUsers>> call, Response<List<ApiFriendshipsUsers>> response) {
                pendingFriends = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null) {
                    pendingFriends = response.body();
                } else {

                }

                fetchCurrentFriends();
            }

            @Override
            public void onFailure(Call<List<ApiFriendshipsUsers>> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }

    void fetchCurrentFriends()
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
                fetchUsers();
            }

            @Override
            public void onFailure(Call<List<ApiFriendshipsUsers>> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }
}