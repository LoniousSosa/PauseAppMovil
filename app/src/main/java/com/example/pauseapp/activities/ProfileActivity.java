package com.example.pauseapp.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.StressLevelResponse;
import com.example.pauseapp.model.UserResponse;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends MenuFunction {

    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_TOKEN  = "auth_token";

    private TextView holaNombre, stressAnteriorText, stressActualText, diasActualizar, numActividades;
    private ProgressBar stressBarCurrent, stressBarPrevious;
    private ImageView stressIconCurrent, stressIconPrevious;
    private Button stressButton;

    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupNavigation();

        // Bind views
        stressBarCurrent = findViewById(R.id.stressBarActual);
        stressIconCurrent = findViewById(R.id.actualStressIcon);
        stressBarPrevious = findViewById(R.id.stressBarAnterior);
        stressIconPrevious = findViewById(R.id.previousStressIcon);
        stressButton = findViewById(R.id.stressButton);
        holaNombre = findViewById(R.id.hola_nombre);
        stressAnteriorText = findViewById(R.id.stressAnteriorText);
        stressActualText = findViewById(R.id.stressActualText);
        diasActualizar = findViewById(R.id.diasActualizar);
        numActividades = findViewById(R.id.numActividades);

        // Init service
        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        // Validate token
        String token = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
        if (token.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return;
        }

        // Button to change stress level
        stressButton.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, TestActivity.class))
        );

        // Load data
        fetchUserData(token);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload data whenever returning
        String token = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
        if (!token.isEmpty()) fetchUserData(token);
    }

    private void fetchUserData(String token) {
        authApiService.getUser("Bearer " + token)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            applyUserData(response.body());
                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Error al cargar datos (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this,
                                "Fallo de conexión", Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "fetchUserData onFailure", t);
                    }
                });
    }

    private void applyUserData(UserResponse user) {
        // Nombre, streak y actividades
        holaNombre.setText("Hola " + user.getUsername());
        diasActualizar.setText((user.getStreakDays() != null ? user.getStreakDays() : 0) + " DÍAS");
        numActividades.setText((user.getCompletedActivities() != null ? user.getCompletedActivities() : 0) + " ACTIVIDADES");

        // COMPUTAR NIVELES POR FECHA
        float initialLevel = 0f, currentLevel = 0f;
        List<StressLevelResponse> levels = user.getStressLevels();
        if (levels != null && !levels.isEmpty()) {
            // Ordeno por fecha (ISO-8601 sin zona)
            levels.sort(Comparator.comparing(l ->
                    LocalDateTime.parse(l.getDate())
            ));
            initialLevel = levels.get(0).getLevel();
            currentLevel = levels.get(levels.size() - 1).getLevel();
        }

        // Mostrar texto
        stressAnteriorText.setText("Nivel de estrés anterior: " + Math.round(initialLevel) + "%");
        stressActualText .setText("Nivel de estrés actual: "  + Math.round(currentLevel) + "%");

        // Actualizar progres bars e íconos
        updateBar(stressBarPrevious, stressIconPrevious, Math.round(initialLevel));
        updateBar(stressBarCurrent,  stressIconCurrent,  Math.round(currentLevel));
    }


    private void updateBar(ProgressBar bar, ImageView icon, int level) {
        bar.setProgress(level);
        int width = (level >= 80) ? 874 : level * 11;
        Drawable drawable;
        int iconRes;

        if (level >= 75) {
            drawable = ContextCompat.getDrawable(this, R.drawable.red_progress);
            iconRes  = R.drawable.logo_enfadado;
        } else if (level >= 50) {
            drawable = ContextCompat.getDrawable(this, R.drawable.yellow_progress);
            iconRes  = R.drawable.logo_serio;
        } else if (level >= 25) {
            drawable = ContextCompat.getDrawable(this, R.drawable.green_progress);
            iconRes  = R.drawable.logo_feliz;
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.blue_progress);
            iconRes  = R.drawable.logo_zen;
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width, 90);
        bar.setLayoutParams(lp);
        bar.setProgressDrawable(drawable);
        icon.setImageResource(iconRes);
    }
}