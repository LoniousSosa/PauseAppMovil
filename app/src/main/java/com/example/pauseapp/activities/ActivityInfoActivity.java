package com.example.pauseapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityInfoActivity extends MenuFunction {
    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_DURATION_PREFIX = "activity_duration_";
    private TextView activityTitle;
    private ImageView imageActivity;
    private TextView tiempoEnActividadLabel;
    private TextView tiempoTextView;

    private AuthApiService apiService;
    private String authToken;
    private long activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        setupNavigation();

        activityTitle = findViewById(R.id.activityTitle);
        imageActivity = findViewById(R.id.imageActivity);
        tiempoEnActividadLabel = findViewById(R.id.tiempoEnActividad);
        tiempoTextView = findViewById(R.id.tiempo);

        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId < 0) {
            Toast.makeText(this, "ID de actividad inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        authToken = prefs.getString("auth_token", "");
        apiService = RetrofitClient.getClient().create(AuthApiService.class);
        apiService.getActivityById(activityId, "Bearer " + authToken)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            ActivityResponse act = resp.body();
                            activityTitle.setText(act.getName());
                            Glide.with(ActivityInfoActivity.this)
                                    .load(act.getThumbnailUrl())
                                    .placeholder(R.drawable.actividad4_2)
                                    .into(imageActivity);
                        }
                    }

                    @Override
                    public void onFailure(Call<ActivityResponse> call, Throwable t) { /* opcional Toast */ }
                });

        long savedSeconds = prefs.getLong(KEY_DURATION_PREFIX + activityId, -1L);
        if (savedSeconds >= 0) {
            tiempoTextView.setText(formatSeconds(savedSeconds));
        } else {
            tiempoTextView.setText("--:--");
        }
    }

    private String formatSeconds(long totalSeconds) {
        long mins = totalSeconds / 60;
        long secs = totalSeconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}
