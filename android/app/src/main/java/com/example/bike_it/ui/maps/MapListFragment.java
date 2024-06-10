package com.example.bike_it.ui.maps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bike_it.R;
import com.example.bike_it.responses.ApiFriendshipsUsers;
import com.example.bike_it.responses.ApiMap;
import com.example.bike_it.ui.friends.FriendListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MapListFragment extends Fragment {

    private ListView listViewMaps;

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

        List<ApiMap> maps = new ArrayList<>();
        maps.add(new ApiMap(0, "test"));
        maps.add(new ApiMap(0, "test2"));

        MapListAdapter adapter = new MapListAdapter(requireContext(), R.layout.item_map, maps);
        listViewMaps.setAdapter(adapter);

        return view;
    }
}