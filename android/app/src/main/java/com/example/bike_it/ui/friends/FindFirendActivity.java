package com.example.bike_it.ui.friends;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bike_it.R;
import com.example.bike_it.responses.ApiFriendshipsUsers;

import java.util.ArrayList;
import java.util.List;

public class FindFirendActivity extends AppCompatActivity {

    private EditText editTextUserSearch;
    private ListView listViewUsers;
    private List<ApiFriendshipsUsers> users;

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

        fetchUsers();
    }

    public void fetchUsers()
    {
        users = new ArrayList<>();
        users.add(new ApiFriendshipsUsers("djaltair", "djaltair"));
        users.add(new ApiFriendshipsUsers("redskittlefox", "redskittlefox"));
        updateUsers();
    }

    public void updateUsers()
    {
        List<ApiFriendshipsUsers> filteredUsers = new ArrayList<>(users);
        String t = editTextUserSearch.getText().toString();
        if(t != "")
        {
            filteredUsers.removeIf(x -> !x.getUsername().startsWith(t));
        }

        FriendListAdapter adapter = new FriendListAdapter(this, R.layout.item_friend, filteredUsers);
        listViewUsers.setAdapter(adapter);
    }
}