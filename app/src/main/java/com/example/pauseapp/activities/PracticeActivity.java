package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityRecordCreateRequest;
import com.example.pauseapp.model.ActivityRecordResponse;
import com.example.pauseapp.model.ActivityResponse;
import com.example.pauseapp.model.MediaResponse;
import com.example.pauseapp.model.UserResponse;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeActivity extends MenuFunction {
    private static final String PREFS_NAME = "PauseAppPrefs";
    private TextView timerText;
    private Button startButton, posponeButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;
    private FrameLayout mediaContainer;

    private AuthApiService apiService;
    private String userToken;
    private long activityId;
    private Long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setupNavigation();

        // Bind views
        timerText = findViewById(R.id.timerText);
        posponeButton = findViewById(R.id.posponeButton);
        mediaContainer = findViewById(R.id.mediaContainer);
        startButton = findViewById(R.id.startButton);

        // Get token
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");
        if (userToken == null || userToken.isEmpty()) {
            goToLogin();
            finish();
            return;
        }

        // Initialize API
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        // Get current user to obtain userId
        fetchCurrentUser();

        // Get activityId
        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId < 0) {
            Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load media for the activity
        apiService.getActivityById(activityId, "Bearer " + userToken)
                .enqueue(new Callback<ActivityResponse>() {
                    @Override
                    public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            displayMedia(resp.body());
                        } else {
                            Toast.makeText(PracticeActivity.this,
                                    "Error al cargar actividad", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ActivityResponse> call, Throwable t) {
                        Toast.makeText(PracticeActivity.this,
                                "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Set listeners
        startButton.setOnClickListener(view -> {
            // Start timer when user presses start
            startTimer();
        });
        posponeButton.setOnClickListener(view -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        });
    }

    private void fetchCurrentUser() {
        apiService.getUser("Bearer " + userToken)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            userId = response.body().getId();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        // optional: handle error
                    }
                });
    }

    private void displayMedia(ActivityResponse activity) {
        mediaContainer.removeAllViews();
        MediaResponse media = activity.getMedia();
        if (media != null && media.getUrl().endsWith(".mp4")) {
            VideoView videoView = new VideoView(this);
            videoView.setVideoURI(Uri.parse(media.getUrl()));
            mediaContainer.addView(videoView,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            startButton.setVisibility(Button.GONE);

            videoView.setOnPreparedListener(mp -> {
                timeLeftInMillis = mp.getDuration();
                long seconds = timeLeftInMillis / 1000;
                updateTimerText();
                videoView.start();
                startTimer();
            });
        } else {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mediaContainer.addView(imageView,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);
            String thumb = activity.getThumbnailUrl();
            Glide.with(this)
                    .load(thumb)
                    .placeholder(R.drawable.actividad4_2)
                    .into(imageView);
            startButton.setVisibility(Button.VISIBLE);
            timeLeftInMillis = TimeUnit.MINUTES.toMillis(1);
            updateTimerText();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }
            @Override
            public void onFinish() {
                timerText.setText("00:00");
                if (userId != null) {
                    recordActivityCompletion();
                }
            }
        }.start();
    }

    private void recordActivityCompletion() {
        // Format current timestamp in ISO 8601
        String nowIso = OffsetDateTime
                .now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        ActivityRecordCreateRequest req = new ActivityRecordCreateRequest(
                activityId,
                nowIso,
                true
        );

        apiService.createUserActivityRecord(
                userId,
                req,
                "Bearer " + userToken
        ).enqueue(new Callback<ActivityRecordResponse>() {
            @Override
            public void onResponse(Call<ActivityRecordResponse> call, Response<ActivityRecordResponse> resp) {
                if (resp.isSuccessful()) {
                    Toast.makeText(PracticeActivity.this,
                            "Registro guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PracticeActivity.this,
                            "Error guardando registro",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ActivityRecordResponse> call, Throwable t) {
                Toast.makeText(PracticeActivity.this,
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}