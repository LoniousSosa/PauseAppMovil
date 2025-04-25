package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PresentationActivity extends MenuFunction {

    private Button startButton, infoButton;
    private TextView activityTitle, description;
    private ImageView imageActivity;

    private AuthApiService apiService;
    private String userToken;
    private Long activityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        setupNavigation();

        activityTitle  = findViewById(R.id.activityTitle);
        description    = findViewById(R.id.descripctionActivity);
        imageActivity  = findViewById(R.id.imageActivity);
        startButton    = findViewById(R.id.startButton);
        infoButton     = findViewById(R.id.infoButton);

        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId == -1) {
            Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");

        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        cargarActividadDesdeApi(activityId);

        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(PresentationActivity.this, PracticeActivity.class);
            intent.putExtra("ACTIVITY_ID", activityId);
            startActivity(intent);
        });

        infoButton.setOnClickListener(v -> {
            Intent intent = new Intent(PresentationActivity.this, ActivityInfoActivity.class);
            intent.putExtra("ACTIVITY_ID", activityId);
            startActivity(intent);
        });
    }

    private void cargarActividadDesdeApi(Long id) {
        Call<ActivityResponse> call = apiService.getActivityById(id, "Bearer " + userToken);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ActivityResponse act = response.body();

                    activityTitle.setText(act.getName());
                    description.setText(act.getDescription());

                    Glide.with(PresentationActivity.this)
                            .load(act.getThumbnailUrl())
                            .placeholder(R.drawable.actividad4_2)
                            .error(R.drawable.actividad4_2)
                            .into(imageActivity);

                } else {
                    Toast.makeText(PresentationActivity.this,
                            "Error al cargar la actividad", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ActivityResponse> call, Throwable t) {
                Toast.makeText(PresentationActivity.this,
                        "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
