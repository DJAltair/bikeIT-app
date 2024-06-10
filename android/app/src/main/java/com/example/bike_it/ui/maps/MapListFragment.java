package com.example.bike_it.ui.maps;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.responses.ApiMap;
import com.example.bike_it.ui.friends.FriendListAdapter;
import com.example.bike_it.ui.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapListFragment extends Fragment {

    private ListView listViewMaps;
    List<ApiMap> maps;

    public MapListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_list, container, false);

        listViewMaps = view.findViewById(R.id.listViewMaps);

        maps = new ArrayList<>();

        fetchMaps();

        return view;
    }

    void updateMapsViews()
    {
        MapListAdapter adapter = new MapListAdapter(requireContext(), R.layout.item_map, maps);
        listViewMaps.setAdapter(adapter);
    }

    void fetchMaps()
    {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<List<ApiMap>> call = apiService.getAllMaps();
        call.enqueue(new Callback<List<ApiMap>>() {
            @Override
            public void onResponse(Call<List<ApiMap>> call, Response<List<ApiMap>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    maps = response.body();
                } else {
                    maps = new ArrayList<>();
                }

                updateMapsViews();
            }

            @Override
            public void onFailure(Call<List<ApiMap>> call, Throwable t) {
                Log.d("MapFragment", "onFailure: " + t.getMessage());
            }
        });
    }
}