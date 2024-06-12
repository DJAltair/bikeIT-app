package com.example.bike_it.ui.gallery;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import java.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.location.Location;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.databinding.FragmentGalleryBinding;
import com.example.bike_it.requests.MapRequest;
import com.example.bike_it.responses.ApiMap;
import com.example.bike_it.responses.ApiPostsResponse;
import com.example.bike_it.ui.post.Post;
import com.example.bike_it.ui.post.PostAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryFragment extends Fragment implements OnMapReadyCallback {

    private FragmentGalleryBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private boolean isRecording = false;
    private List<LatLng> pathPoints = new ArrayList<>();
    private Polyline polyline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.gmap);

        mapFragment.getMapAsync(this);

        Button startButton = view.findViewById(R.id.startButton);
        Button stopButton = view.findViewById(R.id.stopButton);

        startButton.setOnClickListener(v -> startRecording());
        stopButton.setOnClickListener(v -> stopRecording());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (isRecording) {
                            pathPoints.add(currentLatLng);
                            drawPath();
                        }
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    }
                }
            }
        };
    }

    private void startRecording() {
        if (!isRecording) {
            isRecording = true;
            pathPoints.clear();
            startLocationUpdates();
        }
    }

    private void stopRecording() {
        if (isRecording) {
            isRecording = false;
            stopLocationUpdates();
            captureMap();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void drawPath() {
        if (polyline != null) {
            polyline.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(pathPoints)
                .color(Color.RED);  // Set the color to red
        polyline = mMap.addPolyline(polylineOptions);
    }

    private void captureMap() {
        if (mMap == null || pathPoints.isEmpty()) return;

        // Create a bounds builder
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : pathPoints) {
            builder.include(point);
        }

        // Get the bounds
        LatLngBounds bounds = builder.build();

        // Padding to ensure the entire path is visible
        int padding = 100; // in pixels

        // Adjust camera to include entire path
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        postMapServer(bitmap);
                    }
                });
            }

            @Override
            public void onCancel() {
                Log.e("MapFragment", "Camera animation cancelled");
            }
        });
    }

    private void postMapServer(Bitmap bitmap)
    {
        String base64String = convertBitmapToBase64(bitmap);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<ApiMap> call = apiService.createMap(new MapRequest(base64String, ""));

        call.enqueue(new Callback<ApiMap>() {
            @Override
            public void onResponse(Call<ApiMap> call, Response<ApiMap> response) {
                Toast.makeText(getActivity(), "Journey has been saved.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ApiMap> call, Throwable t) {
                // Handle network failure
                // Toast.makeText(PostListFragment.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.d("MapViewer", "onFailure: " + t.getMessage());
            }
        });
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(byteArray);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }
}