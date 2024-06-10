package com.example.bike_it.ui.maps;

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
import com.example.bike_it.responses.ApiMap;

import java.util.List;


public class MapListAdapter extends ArrayAdapter<ApiMap> {
    private Context mContext;
    private int mResource;

    public MapListAdapter(@NonNull Context context, int resource, @NonNull List<ApiMap> objects) {
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

        ApiMap map = getItem(position);

        TextView textViewMapName = listItemView.findViewById(R.id.textViewMapName);
        textViewMapName.setText(map.getName());

        return listItemView;
    }
}
