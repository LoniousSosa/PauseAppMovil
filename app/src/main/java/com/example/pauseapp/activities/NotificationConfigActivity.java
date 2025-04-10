package com.example.pauseapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;

import java.util.HashMap;
import java.util.Map;

public class NotificationConfigActivity extends AppCompatActivity {

    Button okButton, exitButtonNoti;
    CheckBox checkBoxActivas, checkBoxDesactivadas, noMolestarActivo, noMolestarDesactivado;
    private static final String PREFS_NAME = "PauseAppPrefs";
    private LinearLayout currentVisibleButtons = null;

    private Map<Button, LinearLayout> notiOptions;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_config);

        okButton = findViewById(R.id.okButton);
        exitButtonNoti = findViewById(R.id.exitButton);
        checkBoxActivas = findViewById(R.id.checkBoxActivas);
        checkBoxDesactivadas = findViewById(R.id.checkBoxDesactivadas);
        noMolestarActivo = findViewById(R.id.noMolestarActivo);
        noMolestarDesactivado = findViewById(R.id.noMolestarDesactivado);

        notiOptions = new HashMap<>();
        notiOptions.put(findViewById(R.id.statusButton), findViewById(R.id.statusCheckBoxes));
        notiOptions.put(findViewById(R.id.modoNotificaciones), findViewById(R.id.noMolestarCheckboxes));
        notiOptions.put(findViewById(R.id.intervalo), findViewById(R.id.timerLayout));

        for (Map.Entry<Button,LinearLayout> entry:
                notiOptions.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                toggleButtonsVisibility(entry.getValue());
            });
        }

        loadPreferences();

        checkNotificationPermission();

        setMutualCheckboxes(checkBoxActivas, checkBoxDesactivadas);
        setMutualCheckboxes(checkBoxDesactivadas, checkBoxActivas);
        setMutualCheckboxes(noMolestarActivo, noMolestarDesactivado);
        setMutualCheckboxes(noMolestarDesactivado, noMolestarActivo);


        okButton.setOnClickListener(view -> {
            savePreferences();
            if (checkBoxDesactivadas.isChecked()) {
                // Si se desactivan las notificaciones
                Toast.makeText(this, "Las notificaciones han sido desactivadas. " +
                        "Las configuraciones se han guardado.", Toast.LENGTH_LONG).show();
            } else {
                // Si se activan las notificaciones
                Toast.makeText(this, "Las notificaciones han sido activadas. " +
                        "Las configuraciones se han guardado.", Toast.LENGTH_LONG).show();
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finish();
        });

        exitButtonNoti.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                .setTitle("Salir sin guardar")
                .setMessage("¿Estás seguro de que quieres salir? Los cambios no se guardarán.")
                .setPositiveButton("Salir", (dialog, which) -> finish())
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show());
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("activas", checkBoxActivas.isChecked());
        editor.putBoolean("desactivadas", checkBoxDesactivadas.isChecked());
        editor.putBoolean("noMolestarActivo", noMolestarActivo.isChecked());
        editor.putBoolean("noMolestarDesactivado", noMolestarDesactivado.isChecked());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean activas = prefs.getBoolean("activas", false);
        boolean desactivadas = prefs.getBoolean("desactivadas", false);


        noMolestarActivo.setChecked(prefs.getBoolean("noMolestarActivo", false));
        noMolestarDesactivado.setChecked(prefs.getBoolean("noMolestarDesactivado", false));

        if (!activas && !desactivadas) {
            checkNotificationPermission();
        } else {
            checkBoxActivas.setChecked(activas);
            checkBoxDesactivadas.setChecked(desactivadas);
        }
    }

    private void toggleButtonsVisibility(LinearLayout selectedForm) {
        if (currentVisibleButtons != null) {
            currentVisibleButtons.setVisibility(View.GONE);
        }
        if (currentVisibleButtons != selectedForm) {
            selectedForm.setVisibility(View.VISIBLE);
            currentVisibleButtons = selectedForm;
        } else {
            currentVisibleButtons = null;
        }
    }

    private void checkNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();

        if (getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("activas", false) == false &&
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getBoolean("desactivadas", false) == false) {
            // Si las notificaciones están habilitadas, selecciona la CheckBox "activas"
            if (areNotificationsEnabled) {
                checkBoxActivas.setChecked(true);
                checkBoxDesactivadas.setChecked(false);
            } else {
                checkBoxActivas.setChecked(false);
                checkBoxDesactivadas.setChecked(true);
            }
        }
    }

    private void setMutualCheckboxes(CheckBox selected, CheckBox other) {
        selected.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                other.setChecked(false);
            }
        });
    }

}