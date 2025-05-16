package com.example.pauseapp.activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.ActivityResponse;
import com.example.pauseapp.otros.AlertWorker;
import com.example.pauseapp.fragments.PremiumDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LobbyActivity extends MenuFunction {
    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_NOTIFICATION_PERMISSION_SHOWN = "notification_permission_shown";
    private static final String NOTIFICATION_CHANNEL_ID = "alert_channel";
    private static final String EXTRA_INITIAL_FILTERS = "INITIAL_FILTER_TYPE_IDS";

    private ImageButton[] activityButtons;
    private AuthApiService apiService;
    private String userToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_item_background);
        setupNavigation();

        activityButtons = new ImageButton[]{
                findViewById(R.id.primeraActivity),
                findViewById(R.id.segundaActivity),
                findViewById(R.id.terceraActivity),
                findViewById(R.id.cuartaActivity),
                findViewById(R.id.quintaActivity)
        };

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userToken = prefs.getString("auth_token", "");
        if (userToken == null || userToken.isEmpty()) {
            goToLogin();
            finish();
            return;
        }

        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        // Revisar filtros iniciales pasados desde InitialFiltersActivity
        ArrayList<String> initialFilterStrs = getIntent().getStringArrayListExtra(EXTRA_INITIAL_FILTERS);
        if (initialFilterStrs != null && !initialFilterStrs.isEmpty()) {
            List<Long> typeIds = initialFilterStrs.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            fetchActivitiesByType(typeIds);
        } else {
            fetchRecommendedActivities();
        }

        scheduleAlertWorker();
        requestNotificationPermissionIfNeeded(prefs);
    }

    private void fetchActivitiesByType(List<Long> typeIds) {
        apiService.getActivitiesByType("Bearer " + userToken, typeIds)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<List<ActivityResponse>> call,
                                           Response<List<ActivityResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            populateActivityButtons(response.body());
                        } else {
                            fetchRandomActivities();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ActivityResponse>> call, Throwable t) {
                        fetchRandomActivities();
                    }
                });
    }

    private void fetchRecommendedActivities() {
        apiService.getRecommendedActivities("Bearer " + userToken)
                .enqueue(new Callback<List<ActivityResponse>>() {
                    @Override
                    public void onResponse(Call<List<ActivityResponse>> call,
                                           Response<List<ActivityResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            populateActivityButtons(response.body());
                        } else {
                            fetchRandomActivities();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ActivityResponse>> call, Throwable t) {
                        fetchRandomActivities();
                    }
                });
    }

    private void fetchRandomActivities() {
        apiService.getActivitiesByType("Bearer " + userToken, null)
                .enqueue(new Callback<List<ActivityResponse>>() {
                    @Override
                    public void onResponse(Call<List<ActivityResponse>> call,
                                           Response<List<ActivityResponse>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            List<ActivityResponse> all = response.body();
                            Collections.shuffle(all);
                            List<ActivityResponse> subset = all.size() > activityButtons.length
                                    ? all.subList(0, activityButtons.length)
                                    : all;
                            populateActivityButtons(subset);
                        } else {
                            Toast.makeText(LobbyActivity.this,
                                    "No hay actividades disponibles",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ActivityResponse>> call, Throwable t) {
                        Toast.makeText(LobbyActivity.this,
                                "Error de conexión uwu", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void populateActivityButtons(List<ActivityResponse> activities) {
        for (int i = 0; i < activityButtons.length; i++) {
            ImageButton btn = activityButtons[i];
            if (i < activities.size()) {
                ActivityResponse act = activities.get(i);
                Glide.with(this)
                        .load(act.getThumbnailUrl())
                        .placeholder(R.drawable.actividad4_2)
                        .into(btn);
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(view -> {
                    // 1) Leemos si el usuario está suscrito
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    boolean userSubscribed = prefs.getBoolean("subscription", false);

                    // 2) Si es premium y no suscrito, mostramos el diálogo
                    if (Boolean.TRUE.equals(act.getIsPremium()) && !userSubscribed) {
                        PremiumDialogFragment
                                .newInstance(act.getThumbnailUrl())
                                .show(getSupportFragmentManager(), "premiumDlg");
                    } else {
                        // 3) En caso contrario, navegamos a PresentationActivity
                        Intent intent = new Intent(LobbyActivity.this,
                                PresentationActivity.class);
                        intent.putExtra("ACTIVITY_ID", act.getId());
                        startActivity(intent);
                    }
                });

            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    private void scheduleAlertWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest work = new PeriodicWorkRequest.Builder(
                AlertWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                        "AlertCheckWork",
                        ExistingPeriodicWorkPolicy.KEEP,
                        work);
    }

    private void requestNotificationPermissionIfNeeded(SharedPreferences prefs) {
        boolean shown = prefs.getBoolean(KEY_NOTIFICATION_PERMISSION_SHOWN, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
                && !shown) {
            new AlertDialog.Builder(this)
                    .setMessage("Necesitamos este permiso para enviarte notificaciones importantes.")
                    .setPositiveButton("Aceptar", (d,w) -> {
                        ActivityCompat.requestPermissions(
                                LobbyActivity.this,
                                new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                                1001);
                        prefs.edit().putBoolean(KEY_NOTIFICATION_PERMISSION_SHOWN, true).apply();
                    })
                    .setNegativeButton("Cancelar", (d,w) ->
                            Toast.makeText(this,
                                    "Permiso denegado. Las notificaciones no funcionarán.",
                                    Toast.LENGTH_SHORT).show()
                    ).show();

            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Notificaciones de Alertas",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal de notificaciones para alertas de la API");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] grantResults) {
        super.onRequestPermissionsResult(req, perms, grantResults);
        if (req == 1001) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                        "Permiso de notificaciones concedido",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "Permiso de notificaciones denegado",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}