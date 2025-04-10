package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.StressLevelResponse;
import com.example.pauseapp.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends MenuFunction {

    private ProgressBar stressBarCurrent, stressBarPrevious;
    private ImageView stressIconCurrent, stressIconPrevious;

    private int stressLevelCurrent,stressLevelPrevious;

    private AuthApiService authApiService;
    private Button stressButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupNavigation();
        stressBarCurrent = findViewById(R.id.stressBarActual);
        stressIconCurrent = findViewById(R.id.actualStressIcon);
        stressBarPrevious = findViewById(R.id.stressBarAnterior);
        stressIconPrevious = findViewById(R.id.previousStressIcon);

        ImageView stressIcon = findViewById(R.id.previousStressIcon);
        stressButton = findViewById(R.id.stressButton);

        int userId = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).getInt("user_id", -1);


        stressLevelCurrent = obtenerNivelDeEstresActual(userId);
         stressLevelPrevious = obtenerNivelDeEstresAnterior();

        fetchUserData();
        actualizarBarrasDeEstres();

        stressButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this,TestActivity.class);
            startActivity(intent);
        });
    }

    private int obtenerNivelDeEstresActual(int userId) {
        AuthApiService apiService = RetrofitClient.getClient().create(AuthApiService.class);
        Call<StressLevelResponse> call = apiService.getStressLvl(userId,getUserToken());

              call.enqueue(new Callback<>() {
                  @Override
                  public void onResponse(Call<StressLevelResponse> call, Response<StressLevelResponse> response) {
                      if (response.isSuccessful() && response.body() != null) {
                          stressLevelCurrent = response.body().getStressLevel();
                          actualizarBarrasDeEstres();
                      } else {
                          Log.e("ProfileActivity", "Error en la respuesta de la API");
                      }
                  }

                  @Override
                  public void onFailure(Call<StressLevelResponse> call, Throwable t) {
                      Log.e("ProfileActivity", "Error de conexión", t);
                  }
              });


        return TestActivity.getStressLvl(); // Simulación: estrés actual en 65%
    }

    private int obtenerNivelDeEstresAnterior() {
        return 100;
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarBarrasDeEstres();
    }

    private void actualizarBarrasDeEstres() {
        int stressLevelCurrent = obtenerNivelDeEstresActual(1);
        int stressLevelPrevious = obtenerNivelDeEstresAnterior();

        actualizarBarraEstres(stressBarCurrent, stressIconCurrent, stressLevelCurrent);
        actualizarBarraEstres(stressBarPrevious, stressIconPrevious, stressLevelPrevious);
    }
    private void actualizarBarraEstres(ProgressBar progressBar, ImageView stressIcon, int stressLevel) {
        progressBar.setProgress(stressLevel);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(340, 45);


        System.out.println(progressBar.getProgress() + ": PROGRESO");

        Drawable progressDrawable;
        int iconResource;
        int stressWidth = stressLevel * 11;

        if (stressLevel >= 75) {
            if (stressLevel >=80){
               stressWidth = 874;
            }
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.red_progress);
            iconResource = R.drawable.logo_enfadado;

        } else if (stressLevel >= 50) {
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.yellow_progress);
            iconResource = R.drawable.logo_serio;

        } else if (stressLevel >= 25) {
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.green_progress);
            iconResource = R.drawable.logo_feliz;

        } else {
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.blue_progress);
            iconResource = R.drawable.logo_zen;
        }

        params = new LinearLayout.LayoutParams(stressWidth, 90);
        progressBar.setLayoutParams(params);

        progressBar.setProgressDrawable(progressDrawable);
        stressIcon.setImageResource(iconResource);
    }

    private void fetchUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("auth_token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        authApiService.getUser("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    stressLevelCurrent = Integer.parseInt(user.getActualStressLevel());
                    stressLevelPrevious = Integer.parseInt(user.getInitialStressLevel());
                    actualizarBarrasDeEstres();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUserToken() {
        return getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).getString("user_token", "");
    }


}