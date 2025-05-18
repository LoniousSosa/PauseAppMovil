package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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
    private FrameLayout mediaContainer;
    private VideoView videoView;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;

    private AuthApiService apiService;
    private String userToken;
    private long activityId;
    private Long userId;
    private ActivityResponse currentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setupNavigation();

        timerText      = findViewById(R.id.timerText);
        posponeButton  = findViewById(R.id.posponeButton);
        mediaContainer = findViewById(R.id.mediaContainer);
        startButton    = findViewById(R.id.startButton);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");
        if (userToken == null || userToken.isEmpty()) {
            goToLogin();
            finish();
            return;
        }

        apiService = RetrofitClient.getClient().create(AuthApiService.class);
        fetchCurrentUser();

        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId < 0) {
            Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadActivityAndMedia();

        startButton.setOnClickListener(v -> {
            startButton.setVisibility(View.GONE);
            if (videoView != null) videoView.start();
            startTimer();
        });

        posponeButton.setOnClickListener(v -> {
            if (countDownTimer != null) countDownTimer.cancel();
            if (videoView != null && videoView.isPlaying()) {
                videoView.pause();
            }
            startButton.setVisibility(View.VISIBLE);
        });
    }

    private void fetchCurrentUser() {
        apiService.getUser("Bearer " + userToken)
                .enqueue(new Callback<UserResponse>() {
                    @Override public void onResponse(Call<UserResponse> c, Response<UserResponse> r) {
                        if (r.isSuccessful() && r.body() != null) {
                            userId = r.body().getId();
                        }
                    }
                    @Override public void onFailure(Call<UserResponse> c, Throwable t) { }
                });
    }

    private void loadActivityAndMedia() {
        apiService.getActivityById(activityId, "Bearer " + userToken)
                .enqueue(new Callback<ActivityResponse>() {
                    @Override public void onResponse(Call<ActivityResponse> c, Response<ActivityResponse> r) {
                        if (!r.isSuccessful() || r.body() == null) {
                            Toast.makeText(PracticeActivity.this, "Error al cargar actividad", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        currentActivity = r.body();
                        Long mediaId = currentActivity.getMedia() != null
                                ? currentActivity.getMedia().getId()
                                : null;
                        if (mediaId != null) {
                            apiService.getMediaById(mediaId, "Bearer " + userToken)
                                    .enqueue(new Callback<MediaResponse>() {
                                        @Override public void onResponse(Call<MediaResponse> c2, Response<MediaResponse> r2) {
                                            String url = (r2.isSuccessful() && r2.body() != null)
                                                    ? r2.body().getUrl()
                                                    : null;
                                            displayMedia(url);
                                        }
                                        @Override public void onFailure(Call<MediaResponse> c2, Throwable t) {
                                            displayMedia(null);
                                        }
                                    });
                        } else {
                            displayMedia(null);
                        }
                    }
                    @Override public void onFailure(Call<ActivityResponse> c, Throwable t) {
                        Toast.makeText(PracticeActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayMedia(String mediaUrl) {
        mediaContainer.removeAllViews();
        videoView = new VideoView(this);
        mediaContainer.addView(videoView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        startButton.setVisibility(View.VISIBLE);
        posponeButton.setVisibility(View.VISIBLE);

        videoView.setOnErrorListener((mp, what, extra) -> {
            showThumbnail();
            return true;
        });

        videoView.setOnPreparedListener(mp -> {
            timeLeftInMillis = mp.getDuration();
            updateTimerText();
        });

        if (mediaUrl != null && mediaUrl.startsWith("http")) {
            videoView.setVideoURI(Uri.parse(mediaUrl));
        } else if (mediaUrl != null && mediaUrl.startsWith("/")) {
            String filename = mediaUrl.substring(mediaUrl.lastIndexOf('/') + 1,
                    mediaUrl.lastIndexOf('.'));
            int resId = getResources().getIdentifier(filename, "raw", getPackageName());
            if (resId != 0) {
                Uri rawUri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
                videoView.setVideoURI(rawUri);
            } else {
                showThumbnail();
            }
        } else {
            showThumbnail();
        }
    }

    private void showThumbnail() {
        mediaContainer.removeAllViews();
        ImageView iv = new ImageView(this);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mediaContainer.addView(iv,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );
        Glide.with(this)
                .load(currentActivity.getThumbnailUrl())
                .placeholder(R.drawable.actividad4_2)
                .into(iv);

        startButton.setVisibility(View.VISIBLE);
        posponeButton.setVisibility(View.GONE);
        timeLeftInMillis = TimeUnit.MINUTES.toMillis(1);
        updateTimerText();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override public void onTick(long ms) {
                timeLeftInMillis = ms;
                updateTimerText();
            }
            @Override public void onFinish() {
                timerText.setText("00:00");
                createThenCompleteAndExit();
            }
        }.start();
    }

    private void createThenCompleteAndExit() {
        // timestamp ISO UTC
        String nowIso = OffsetDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        // 1) Intentar crear registro (si ya existe, seguiremos de todos modos)
        ActivityRecordCreateRequest createReq =
                new ActivityRecordCreateRequest(activityId, nowIso, false);
        apiService.createUserActivityRecord(userId, createReq, "Bearer " + userToken)
                .enqueue(new Callback<ActivityRecordResponse>() {
                    @Override public void onResponse(Call<ActivityRecordResponse> c1, Response<ActivityRecordResponse> r1) {
                        // pase lo que pase, completamos
                        completeAndGoLobby();
                    }
                    @Override public void onFailure(Call<ActivityRecordResponse> c1, Throwable t) {
                        // Si fallo 409 (ya existe), seguimos; sino, mostramos error
                        if (t.getMessage().contains("409")) {
                            completeAndGoLobby();
                        } else {
                            Toast.makeText(PracticeActivity.this,
                                    "Error creando registro: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void completeAndGoLobby() {
        apiService.completeActivity(userId, activityId, "Bearer " + userToken)
                .enqueue(new Callback<ActivityRecordResponse>() {
                    @Override public void onResponse(Call<ActivityRecordResponse> c2, Response<ActivityRecordResponse> r2) {
                        if (r2.isSuccessful()) {
                            Intent i = new Intent(PracticeActivity.this, LobbyActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(PracticeActivity.this,
                                    "Error completando actividad: " + r2.code(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override public void onFailure(Call<ActivityRecordResponse> c2, Throwable t) {
                        Toast.makeText(PracticeActivity.this,
                                "Error de red: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateTimerText() {
        int m = (int)(timeLeftInMillis / 1000) / 60;
        int s = (int)(timeLeftInMillis / 1000) % 60;
        timerText.setText(String.format("%02d:%02d", m, s));
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}