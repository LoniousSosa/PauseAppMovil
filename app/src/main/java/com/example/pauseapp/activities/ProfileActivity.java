package com.example.pauseapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends MenuFunction {

    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_TOKEN  = "auth_token";

    private TextView holaNombre, stressAnteriorText, stressActualText,diasActualizar,numActividades;
    private ProgressBar stressBarCurrent, stressBarPrevious;
    private ImageView stressIconCurrent, stressIconPrevious;
    private Button stressButton;

    private AuthApiService authApiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupNavigation();

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

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        authToken = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
        if (authToken == null) {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
            return;
        }

        stressButton.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, TestActivity.class))
        );
        fetchUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cada vez que vuelve a esta pantalla, recarga datos
        fetchUserData();
    }

    private void fetchUserData() {
        String token = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
        if (token.isEmpty()) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

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
                                "Fallo de conexión",
                                Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "onFailure fetchUserData", t);
                    }
                });
    }


    private void applyUserData(UserResponse user) {
        // nombre, días, actividades…
        holaNombre.setText("Hola " + user.getName());
        diasActualizar .setText(String.valueOf(user.getStreakDays()));
        numActividades.setText(String.valueOf(user.getCompletedActivities()));

        // si tenemos stressLevels, el primero es el inicial y el último es el current
        float initial = 0f, current = 0f;
        List<StressLevelResponse> levels = user.getStressLevels();
        Log.e("comprobacion","no entra al if");
        if (levels != null && !levels.isEmpty()) {
            initial = levels.get(0).getLevel();
            Log.e("inicial",initial+ " inicial");
            current = levels.get(levels.size() - 1).getLevel();
            System.out.println(current);
        }

        stressAnteriorText.setText("Nivel de estrés anterior: " + Math.round(initial) + "%");
        stressActualText .setText("Nivel de estrés actual: "    + Math.round(current) + "%");

        updateBar(stressBarPrevious, stressIconPrevious, (int) initial);
        updateBar(stressBarCurrent,  stressIconCurrent,  (int) current);
    }

    private int safeParse(String s) {
        try { return Integer.parseInt(s); }
        catch (Exception e) { return 0; }
    }

    private void updateBar(ProgressBar bar, ImageView icon, int level) {
        bar.setProgress(level);
        int width = (level >= 80)
                ? 874
                : level * 11;
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