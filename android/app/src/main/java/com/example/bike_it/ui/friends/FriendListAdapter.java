package com.example.bike_it.ui.friends;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.R;

import java.util.List;

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

        return listItemView;
    }
}