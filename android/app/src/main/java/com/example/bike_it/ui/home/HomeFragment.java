package com.example.bike_it.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.bike_it.ApiClient;
import com.example.bike_it.ApiService;
import com.example.bike_it.R;
import com.example.bike_it.databinding.FragmentHomeBinding;
import com.example.bike_it.requests.GlobalNotificationRequest;
import com.example.bike_it.responses.ApiNotification;
import com.example.bike_it.ui.friends.FindFirendActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Button notifyButton = root.findViewById(R.id.makeGlobalNotificationButton);
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewNotification();
            }
        });

        return root;
    }

    void makeNewNotification()
    {
        EditText notifyText = getView().findViewById(R.id.editTextNotificationContents);

        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        String notifString = notifyText.getText().toString();
        Call<ResponseBody> call = apiService.createNotification(new GlobalNotificationRequest(notifString));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("Notification", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}