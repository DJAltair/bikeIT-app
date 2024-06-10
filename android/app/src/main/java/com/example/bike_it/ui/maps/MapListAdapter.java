package com.example.bike_it.ui.maps;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.R;
import com.example.bike_it.responses.ApiMap;
import com.example.bike_it.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
        textViewMapName.setText(map.getCreatedAt());

        if(map.getImage() != null)
        {
            ImageView imageview = listItemView.findViewById(R.id.imageViewMap);
            imageview.setImageBitmap(map.getImage());
        }

        Button shareMapButton = listItemView.findViewById(R.id.buttonShareMap);
        shareMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareMapIntent(map.getId());
            }
        }
        );

        Button deleteMapButton = listItemView.findViewById(R.id.buttonDeleteMap);
        deleteMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMap(map.getId());
                remove(map);
            }
        }
        );

        return listItemView;
    }

    public void shareMapIntent(int mapId)
    {
        Intent i = new Intent(getContext(), ShareMapActivity.class);
        i.putExtra("map_id", mapId);
        getContext().startActivity(i);
    }

    public void deleteMap(int mapId)
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<ResponseBody> call = apiService.deleteMap(mapId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("MapFragment", "onFailure: " + t.getMessage());
            }
        });
    }

}
