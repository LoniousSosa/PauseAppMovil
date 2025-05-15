package com.example.pauseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    private TextView activityTitle, description, tvReadMore;
    private ImageView imageActivity;

    private boolean isDescriptionExpanded = false;
    private AuthApiService apiService;
    private String userToken;
    private Long activityId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);

        // 1. Binding de vistas
        activityTitle   = findViewById(R.id.activityTitle);
        description     = findViewById(R.id.descripctionActivity);
        imageActivity   = findViewById(R.id.imageActivity);
        startButton     = findViewById(R.id.startButton);
        infoButton      = findViewById(R.id.infoButton);
        tvReadMore   = findViewById(R.id.tvReadMore);

        // 2. Configurar drawer y navegación inferior
        setupNavigation();
        description.setMaxLines(3);
        description.setEllipsize(TextUtils.TruncateAt.END);
        tvReadMore.setText("Leer más");

        // 3. Obtener ID de actividad del Intent
        activityId = getIntent().getLongExtra("ACTIVITY_ID", -1L);
        if (activityId == -1L) {
            Toast.makeText(this, "Actividad no válida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 4. Obtener token y validar existencia
        userToken = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE)
                .getString("auth_token", "");
        if (userToken == null || userToken.isEmpty()) {
            goToLogin();
            finish();
            return;
        }

        // 5. Inicializar servicio Retrofit
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        // 6. Cargar detalles de la actividad
        fetchActivityDetails(activityId);

        // 7. Listeners
        startButton.setOnClickListener(view -> {
            // aquí podrías registrar el inicio de la actividad si lo necesitas
            Intent intent = new Intent(PresentationActivity.this, PracticeActivity.class);
            intent.putExtra("ACTIVITY_ID", activityId);
            startActivity(intent);
        });

        infoButton.setOnClickListener(view -> {
            Intent intent = new Intent(PresentationActivity.this, ActivityInfoActivity.class);
            intent.putExtra("ACTIVITY_ID", activityId);
            startActivity(intent);
        });

        tvReadMore.setOnClickListener(view -> {
            if (!isDescriptionExpanded) {
                description.setMaxLines(Integer.MAX_VALUE);
                description.setEllipsize(null);
                tvReadMore.setText("Leer menos");
                isDescriptionExpanded = true;
            } else {
                description.setMaxLines(3);
                description.setEllipsize(TextUtils.TruncateAt.END);
                tvReadMore.setText("Leer más");
                isDescriptionExpanded = false;
            }
        });

    }

    /**
     * Hace GET /activity/{id} y muestra los datos en pantalla.
     */
    private void fetchActivityDetails(Long id) {
        Call<ActivityResponse> call = apiService.getActivityById(id, "Bearer " + userToken);
        call.enqueue(new Callback<ActivityResponse>() {
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
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Redirige al usuario a Login si no hay token válido.
     */
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}