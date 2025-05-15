package com.example.pauseapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.pauseapp.R;
import com.example.pauseapp.otros.AlertWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NotificationConfigActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_NOTIFICATION_INTERVAL = "notification_interval_hours";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int REQ_NOTIFY = 1001;


    private Button okButton, exitButtonNoti, testBtn,shortcut;
    private CheckBox checkBoxActivas, checkBoxDesactivadas, noMolestarActivo, noMolestarDesactivado;
    private CheckBox cadaOcho, cadaDoce, cadaVeinticuatro;
    private LinearLayout currentVisibleButtons;
    private Map<Button, LinearLayout> notiOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_config);

        okButton = findViewById(R.id.okButton);
        exitButtonNoti = findViewById(R.id.exitButton);
        testBtn = findViewById(R.id.btnTestNotification);


        checkBoxActivas = findViewById(R.id.checkBoxActivas);
        checkBoxDesactivadas = findViewById(R.id.checkBoxDesactivadas);
        noMolestarActivo = findViewById(R.id.noMolestarActivo);
        noMolestarDesactivado = findViewById(R.id.noMolestarDesactivado);

        cadaOcho = findViewById(R.id.cadaocho);
        cadaDoce = findViewById(R.id.cadadoce);
        cadaVeinticuatro = findViewById(R.id.cadaveinticuatro);

        shortcut = findViewById(R.id.createAlertShortcut);

        notiOptions = new HashMap<>();
        notiOptions.put(findViewById(R.id.statusButton), findViewById(R.id.statusCheckBoxes));
        notiOptions.put(findViewById(R.id.modoNotificaciones), findViewById(R.id.noMolestarCheckboxes));
        notiOptions.put(findViewById(R.id.frecuencia), findViewById(R.id.timerLayout));

        for (Map.Entry<Button, LinearLayout> entry : notiOptions.entrySet()) {
            entry.getKey().setOnClickListener(view -> toggleButtonsVisibility(entry.getValue()));
        }

        setMutualCheckboxes(checkBoxActivas, checkBoxDesactivadas);
        setMutualCheckboxes(checkBoxDesactivadas, checkBoxActivas);
        setMutualCheckboxes(noMolestarActivo, noMolestarDesactivado);
        setMutualCheckboxes(noMolestarDesactivado, noMolestarActivo);

        setMutualIntervalCheckboxes(cadaOcho, cadaDoce, cadaVeinticuatro);
        loadPreferences();
        ensureNotificationPermission();

        okButton.setOnClickListener(view -> {
            savePreferences();
            boolean enabled = checkBoxActivas.isChecked();
            boolean dnd = noMolestarActivo.isChecked();
            int hours;

            if (cadaOcho.isChecked()) hours = 8;
            else if (cadaDoce.isChecked()) hours = 12;
            else hours = 24;

            long periodMinutes = Math.max(15L, hours * 60L);
            WorkManager wm = WorkManager.getInstance(this);
            if (!enabled || dnd) {
                wm.cancelUniqueWork("alert_worker");
            } else {
                PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                        AlertWorker.class, periodMinutes, TimeUnit.MINUTES).build();
                wm.enqueueUniquePeriodicWork(
                        "alert_worker", ExistingPeriodicWorkPolicy.REPLACE, req
                );
            }

            Toast.makeText(this, enabled && !dnd
                            ? "Notificaciones activadas cada " + hours + "h"
                            : "Notificaciones desactivadas", Toast.LENGTH_LONG).show();

            new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1500);
        });

        exitButtonNoti.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle("Salir sin guardar")
                        .setMessage("¿Estás seguro? Los cambios no se guardarán.")
                        .setPositiveButton("Salir", (d,w) -> finish())
                        .setNegativeButton("Cancelar", (d,w) -> d.dismiss())
                        .show()
        );

        shortcut.setOnClickListener(view -> {
            Intent intent = new Intent(this,NewAlertActivity.class);
            startActivity(intent);
        });

        testBtn.setOnClickListener(view -> {
            OneTimeWorkRequest testRequest = new OneTimeWorkRequest.Builder(AlertWorker.class)
                    .build();
            WorkManager.getInstance(this).enqueue(testRequest);
        });
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("activas", checkBoxActivas.isChecked());
        editor.putBoolean("desactivadas", checkBoxDesactivadas.isChecked());
        editor.putBoolean("noMolestarActivo", noMolestarActivo.isChecked());
        editor.putBoolean("noMolestarDesactivado", noMolestarDesactivado.isChecked());

        int intervalHours = cadaOcho.isChecked() ? 8
                : cadaDoce.isChecked() ? 12
                : 24;
        editor.putInt(KEY_NOTIFICATION_INTERVAL, intervalHours);

        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean activas     = prefs.getBoolean("activas", false);
        boolean desactivadas= prefs.getBoolean("desactivadas", false);
        boolean dndActive   = prefs.getBoolean("noMolestarActivo", false);

        checkBoxActivas.setChecked(activas);
        checkBoxDesactivadas.setChecked(desactivadas);
        noMolestarActivo.setChecked(dndActive);
        noMolestarDesactivado.setChecked(!dndActive);

        int savedHours = prefs.getInt(KEY_NOTIFICATION_INTERVAL, 8);
        cadaOcho.setChecked(savedHours == 8);
        cadaDoce.setChecked(savedHours == 12);
        cadaVeinticuatro.setChecked(savedHours == 24);
    }

    private void toggleButtonsVisibility(LinearLayout selectedLayout) {
        if (currentVisibleButtons != null) {
            currentVisibleButtons.setVisibility(LinearLayout.GONE);
        }
        if (currentVisibleButtons != selectedLayout) {
            selectedLayout.setVisibility(LinearLayout.VISIBLE);
            currentVisibleButtons = selectedLayout;
        } else {
            currentVisibleButtons = null;
        }
    }

    private void setMutualCheckboxes(CheckBox first, CheckBox second) {
        first.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) second.setChecked(false);
        });
    }

    private void setMutualIntervalCheckboxes(CheckBox selected, CheckBox other1, CheckBox other2) {
        selected.setOnCheckedChangeListener((btn, isChecked) -> {
            if (isChecked) {
                other1.setChecked(false);
                other2.setChecked(false);
            }
        });
    }

    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{ Manifest.permission.POST_NOTIFICATIONS },
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_NOTIFY) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario ha dado permiso: ya se podrán mostrar notificaciones.
                Toast.makeText(this,
                        "Permiso de notificaciones concedido",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Sin permiso, no se mostrarán.
                Toast.makeText(this,
                        "Sin permiso de notificaciones",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
