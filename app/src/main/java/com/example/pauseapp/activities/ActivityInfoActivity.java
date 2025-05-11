package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityRecordResponse;
import com.example.pauseapp.model.ActivityResponse;
import com.example.pauseapp.model.UserResponse;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityInfoActivity extends MenuFunction {
    private TextView activityTitle;
    private ImageView imageActivity;
    private TextView tiempoEnActividadLabel;
    private TextView tiempoTextView;

    private AuthApiService apiService;
    private String userToken;
    private Long activityId;
    private Long userId;

    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        // 1. Bind views
        activityTitle = findViewById(R.id.activityTitle);
        imageActivity = findViewById(R.id.imageActivity);
        tiempoEnActividadLabel = findViewById(R.id.tiempoEnActividad);
        tiempoTextView = findViewById(R.id.tiempo);

        // 2. Setup navigation
        setupNavigation();

        // 3. Get activityId from intent
        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId < 0) {
            Toast.makeText(this, "ID de actividad inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 4. Get token
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userToken = prefs.getString("user_token", "");
        if (userToken == null || userToken.isEmpty()) {
            goToLogin();
            finish();
            return;
        }

        // 5. Initialize API service
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        // 6. Load activity details
        fetchActivityDetails(activityId);

        // 7. Fetch current user and then activity records
        fetchCurrentUser();
    }

    private void fetchActivityDetails(Long id) {
        apiService.getActivityById(id, "Bearer " + userToken)
                .enqueue(new Callback<ActivityResponse>() {
                    @Override
                    public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ActivityResponse act = response.body();
                            activityTitle.setText(act.getName());
                            Glide.with(ActivityInfoActivity.this)
                                    .load(act.getThumbnailUrl())
                                    .placeholder(R.drawable.actividad4_2)
                                    .into(imageActivity);
                        } else {
                            Toast.makeText(ActivityInfoActivity.this,
                                    "Error al cargar la actividad", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ActivityResponse> call, Throwable t) {
                        Toast.makeText(ActivityInfoActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCurrentUser() {
        apiService.getUser("Bearer " + userToken)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userId = response.body().getId();
                            fetchActivityRecords(userId);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        // opcional manejo de error
                    }
                });
    }

    private void fetchActivityRecords(Long userId) {
        apiService.getUserActivityRecords(userId, "Bearer " + userToken)
                .enqueue(new Callback<List<ActivityRecordResponse>>() {
                    @Override
                    public void onResponse(Call<List<ActivityRecordResponse>> call,
                                           Response<List<ActivityRecordResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<ActivityRecordResponse> records = response.body();
                            String latestTime = null;
                            for (ActivityRecordResponse rec : records) {
                                if (rec.getActivity() != null && rec.getActivity().getId().equals(activityId)) {
                                    latestTime = rec.getTime();
                                    break;
                                }
                            }
                            if (latestTime != null) {
                                try {
                                    OffsetDateTime odt = OffsetDateTime.parse(latestTime, INPUT_FORMATTER);
                                    tiempoTextView.setText(odt.format(OUTPUT_FORMATTER));
                                } catch (Exception e) {
                                    tiempoTextView.setText(latestTime);
                                }
                            } else {
                                tiempoTextView.setText("--:--");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ActivityRecordResponse>> call, Throwable t) {
                        // opcional manejo de error
                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}