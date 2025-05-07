package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;

import java.util.HashSet;
import java.util.Set;

public class InitialFiltersActivity extends AppCompatActivity {

    private static final String PREFS_NAME  = "PauseAppPrefs";
    private static final String KEY_DONE    = "initial_filters_done";
    private static final String KEY_CHOICES = "initial_filters_choices";

    // IDs de cada fila de filtro, coinciden con XML
    private final int[] FILTER_IDS = {
            R.id.filter1,
            R.id.filter2,
            R.id.filter3,
            R.id.filter4,
            R.id.filter5
    };
    // TextViews de cada fila (para leer el texto real)
    private final int[] FILTER_TEXT_IDS = {
            R.id.filter1Text,
            R.id.filter2Text,
            R.id.filter3Text,
            R.id.filter4Text,
            R.id.filter5Text
    };

    private Set<String> selected = new HashSet<>();
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.getBoolean(KEY_DONE, false)) {
            launchNext();
            return;
        }

        setContentView(R.layout.activity_initial_filters);

        // Para cada fila, leer su TextView y montarle el listener
        for (int i = 0; i < FILTER_IDS.length; i++) {
            LinearLayout row = findViewById(FILTER_IDS[i]);
            String label = ((android.widget.TextView)
                    findViewById(FILTER_TEXT_IDS[i]))
                    .getText().toString();

            row.setOnClickListener(v -> {
                if (selected.contains(label)) {
                    selected.remove(label);
                    row.setAlpha(1f);
                } else {
                    selected.add(label);
                    row.setAlpha(0.6f);
                }
            });
        }

        // Botón Continuar
        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            if (selected.isEmpty()) {
                Toast.makeText(this,
                        "Selecciona al menos un filtro",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            // Guardar elecciones + marca de completado
            prefs.edit()
                    .putStringSet(KEY_CHOICES, selected)
                    .putBoolean(KEY_DONE, true)
                    .apply();

            launchNext();
        });
    }

    private void launchNext() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
