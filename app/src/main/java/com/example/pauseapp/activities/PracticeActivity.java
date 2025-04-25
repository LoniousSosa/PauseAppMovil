package com.example.pauseapp.activities;

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

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PracticeActivity extends MenuFunction {
    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_DURATION_PREFIX = "activity_duration_";
    private TextView timerText;

    private Button startButton,posponeButton;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;
    private FrameLayout mediaContainer;

    private AuthApiService apiService;
    private String authToken;
    private long activityId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setupNavigation();

        timerText = findViewById(R.id.timerText);
        posponeButton = findViewById(R.id.posponeButton);
        mediaContainer = findViewById(R.id.mediaContainer);
        startButton = findViewById(R.id.startButton);

        SharedPreferences prefs = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
        authToken = prefs.getString("auth_token", "");
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId < 0) {
            Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        apiService.getActivityById(activityId, "Bearer " + authToken)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ActivityResponse> call, Response<ActivityResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            //Cambio de imagen o video pasandole
                            //el modelo como parametro
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

        startButton.setOnClickListener(v -> startTimer());
        posponeButton.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
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
                saveDurationLocally(seconds);
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
            long seconds = TimeUnit.MINUTES.toSeconds(1);
            saveDurationLocally(seconds);
            updateTimerText();
            startButton.setVisibility(Button.VISIBLE);
        }
    }

    private void saveDurationLocally(long seconds) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putLong(KEY_DURATION_PREFIX + activityId, seconds)
                .apply();
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

                // Obtener userId de SharedPreferences
                SharedPreferences prefs = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
                Long userId = prefs.getLong("user_id", 0L);
                String token = prefs.getString("auth_token", "");

                // Formatea el timestamp actual en ISO 8601
                String nowIso = OffsetDateTime
                        .now(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                ActivityRecordCreateRequest req = new ActivityRecordCreateRequest(
                        activityId,   // viene del Intent
                        nowIso,
                        true         // completada
                );

                // Llama al endpoint POST /user/{userId}/record
                apiService.createUserActivityRecord(
                        userId,
                        req,
                        "Bearer " + token
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

        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }
}