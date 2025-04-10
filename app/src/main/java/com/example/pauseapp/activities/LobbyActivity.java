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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.pauseapp.R;
import com.example.pauseapp.otros.AlertWorker;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class LobbyActivity extends MenuFunction {
    ImageButton primeraActivity, segundaActivity, terceraActivity, cuartaActivity, quintaActivity;
    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_NOTIFICATION_PERMISSION_SHOWN = "notification_permission_shown";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_item_background);
        primeraActivity = findViewById(R.id.primeraActivity);
        segundaActivity = findViewById(R.id.segundaActivity);
        terceraActivity = findViewById(R.id.terceraActivity);
        cuartaActivity = findViewById(R.id.cuartaActivity);
        quintaActivity = findViewById(R.id.quintaActivity);

        setButtonData(primeraActivity, "Meditación", R.drawable.actividad_cuatro);
        setButtonData(segundaActivity, "Relajación", R.drawable.actividad_uno);
        setButtonData(terceraActivity, "Respiración", R.drawable.actividad_cuatro);
        setButtonData(cuartaActivity, "Mindfullness", R.drawable.actividad_uno);
        setButtonData(quintaActivity, "Enfoque", R.drawable.actividad_cuatro);

        primeraActivity.setOnClickListener(this::getActivityInfo);
        segundaActivity.setOnClickListener(this::getActivityInfo);
        terceraActivity.setOnClickListener(this::getActivityInfo);
        cuartaActivity.setOnClickListener(this::getActivityInfo);
        quintaActivity.setOnClickListener(this::getActivityInfo);
        setupNavigation();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest alertWorkRequest =
                new PeriodicWorkRequest.Builder(AlertWorker.class, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "AlertCheckWork",
                ExistingPeriodicWorkPolicy.KEEP,
                alertWorkRequest
        );

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean permissionShown = prefs.getBoolean(KEY_NOTIFICATION_PERMISSION_SHOWN, false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED && !permissionShown) {

            new AlertDialog.Builder(this)
                    .setMessage("Necesitamos este permiso para enviarte notificaciones importantes.")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        ActivityCompat.requestPermissions(LobbyActivity.this,
                                new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(KEY_NOTIFICATION_PERMISSION_SHOWN, true);
                        editor.apply();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        Toast.makeText(LobbyActivity.this, "Permiso denegado. Las notificaciones no funcionarán.", Toast.LENGTH_SHORT).show();
                    })
                    .show();

            //Canal para las notificaciones

            NotificationChannel channel = new NotificationChannel(
                    "alert_channel",
                    "Notificaciones de Alertas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal de notificaciones para alertas de la API");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    private void setButtonData(ImageButton button, String activityName, int imageResource) {
        Bundle data = new Bundle();
        data.putString("NAME", activityName);
        data.putInt("IMAGE", imageResource);
        button.setTag(data);
    }

    public void getActivityInfo(View view) {
        ImageButton clickedButton = (ImageButton) view;
        Bundle data = (Bundle) clickedButton.getTag();
        if (data != null) {
            Intent intent = new Intent(this, PresentationActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}