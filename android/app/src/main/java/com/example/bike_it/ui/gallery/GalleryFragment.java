package com.example.bike_it.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bike_it.databinding.FragmentGalleryBinding;
import com.tomtom.sdk.map.display.TomTomMap;

import com.tomtom.sdk.map.display.route.Route;
import com.tomtom.sdk.map.display.route.RouteOptions;
import com.tomtom.sdk.map.display.common.WidthByZoom;
import com.tomtom.sdk.location.GeoPoint;
import com.tomtom.sdk.map.display.route.Instruction;
import com.tomtom.sdk.common.measures.*;

import com.tomtom.sdk.*;

import java.util.Arrays;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

         // binding.mapFragment.;

        // final TextView textView = binding.textGallery;
        // galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}