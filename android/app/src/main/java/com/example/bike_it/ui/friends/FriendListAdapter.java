package com.example.bike_it.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.requests.UsernameRequest;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.R;
import com.example.bike_it.ui.post.PostActivity;
import com.example.bike_it.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendListAdapter extends ArrayAdapter<ApiFriendshipsUsers> {

    private Context mContext;
    private int mResource;

    public FriendListAdapter(@NonNull Context context, int resource, @NonNull List<ApiFriendshipsUsers> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
        }

        ApiFriendshipsUsers friend = getItem(position);

        TextView textViewFriendName = listItemView.findViewById(R.id.textViewFriendName);
        textViewFriendName.setText(friend.getName());

        listItemView.setClickable(true);
        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, ProfileActivity.class);

                String username = friend.getUsername();

                if(username == null)
                    return;

                i.putExtra("profile_username", username);
                mContext.startActivity(i);
            }
        });

        if(friend.status == ApiFriendshipsUsers.ApiFriendshipsStatus.friend)
        {
            Button deleteFriendBtn = listItemView.findViewById(R.id.buttonDeleteFriend);
            deleteFriendBtn.setVisibility(View.VISIBLE);
            deleteFriendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            doUnfriend(friend);
                        }
                    }
            );
        }
        else if(friend.status == ApiFriendshipsUsers.ApiFriendshipsStatus.requested)
        {
            LinearLayout acceptRejectLayout = listItemView.findViewById(R.id.linearLayoutFriendRequest);
            acceptRejectLayout.setVisibility(View.VISIBLE);

            Button acceptFriendBtn = acceptRejectLayout.findViewById(R.id.buttonAcceptFriendRequest);
            Button rejectFriendBtn = acceptRejectLayout.findViewById(R.id.buttonRejectFriendRequest);
            acceptFriendBtn.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v)
                                                   {
                                                       doAcceptFriendRequest(friend);
                                                   }
                                               }
            );

            rejectFriendBtn.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       doRejectFriendRequest(friend);
                                                   }
                                               }
            );
        }
        else if(friend.status == ApiFriendshipsUsers.ApiFriendshipsStatus.none)
        {
            Button addFriendBtn = listItemView.findViewById(R.id.buttonAddFriend);
            addFriendBtn.setVisibility(View.VISIBLE);
            addFriendBtn.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {

                                                       doBefriend(friend);
                                                   }
                                               }
            );
        }
        return listItemView;
    }

    void doBefriend(ApiFriendshipsUsers user)
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.befriend(new UsernameRequest(user.getUsername()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                remove(user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }

    void doUnfriend(ApiFriendshipsUsers user)
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.unfriend(new UsernameRequest(user.getUsername()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                remove(user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }

    void doAcceptFriendRequest(ApiFriendshipsUsers user)
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.acceptFriendRequest(new UsernameRequest(user.getUsername()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                remove(user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }

    void doRejectFriendRequest(ApiFriendshipsUsers user)
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ResponseBody> call = apiService.denyFriendRequest(new UsernameRequest(user.getUsername()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                remove(user);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("FriendsFragment", "onFailure: " + t.getMessage());

            }
        });
    }
}