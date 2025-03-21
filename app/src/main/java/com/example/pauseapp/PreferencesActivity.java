package com.example.pauseapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class PreferencesActivity extends AppCompatActivity {

    Button okButton, exitButtonNoti;
    CheckBox checkAppMsgs, checkBoxAmigosMsgs, checkboxAmbiental, checkboxAudio, checkboxMotivacion;

    private static final String PREFS_NAME = "PersonalPrefs";

    private LinearLayout currentVisibleButtons = null;

    private Map<Button, LinearLayout> preferencesOptions;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        okButton = findViewById(R.id.okButton);
        exitButtonNoti = findViewById(R.id.exitButtonNoti);
        checkAppMsgs = findViewById(R.id.checkBoxAppMsgs);
        checkBoxAmigosMsgs = findViewById(R.id.checkBoxAmigosMsgs);
        checkboxAmbiental = findViewById(R.id.checboxAmbiental);
        checkboxAudio = findViewById(R.id.checkboxAudio);
        checkboxMotivacion = findViewById(R.id.checkboxMotivation);

        preferencesOptions = new HashMap<>();
        preferencesOptions.put(findViewById(R.id.messagesButton),findViewById(R.id.messagesCheckBoxes));
        preferencesOptions.put(findViewById(R.id.soundButton),findViewById(R.id.soundCheckBoxes));
        preferencesOptions.put(findViewById(R.id.answerButton),findViewById(R.id.motivationalLayout));

        for (Map.Entry<Button,LinearLayout> entry:
                preferencesOptions.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                toggleButtonsVisibility(entry.getValue());
            });
        }

        loadPreferences();

        okButton.setOnClickListener(view -> {
            savePreferences();
            Toast.makeText(this, "Configuración actualizada con éxito", Toast.LENGTH_SHORT).show();
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

        editor.putBoolean("deLaAplicacion", checkAppMsgs.isChecked());
        editor.putBoolean("deLosAmigos", checkBoxAmigosMsgs.isChecked());
        editor.putBoolean("musicaAmbiental", checkboxAmbiental.isChecked());
        editor.putBoolean("audios", checkboxAudio.isChecked());
        editor.putBoolean("motivacionales",checkboxMotivacion.isChecked());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        checkAppMsgs.setChecked(prefs.getBoolean("deLaAplicacion", true));
        checkBoxAmigosMsgs.setChecked(prefs.getBoolean("deLosAmigos", true));
        checkboxAmbiental.setChecked(prefs.getBoolean("musicaAmbiental", true));
        checkboxAudio.setChecked(prefs.getBoolean("audios", true));
        checkboxMotivacion.setChecked(prefs.getBoolean("motivacionales",true));
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
}