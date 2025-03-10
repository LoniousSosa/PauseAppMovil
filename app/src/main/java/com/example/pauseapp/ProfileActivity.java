package com.example.pauseapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends BaseActivity {

    private ProgressBar stressBarCurrent, stressBarPrevious;
    private ImageView stressIconCurrent, stressIconPrevious;

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

        int stressLevelCurrent = obtenerNivelDeEstresActual(); // 0-100
        int stressLevelPrevious = obtenerNivelDeEstresAnterior(); // 0-100

        actualizarBarraEstres(stressBarCurrent, stressIconCurrent, stressLevelCurrent);
        actualizarBarraEstres(stressBarPrevious, stressIconPrevious, stressLevelPrevious);
    }

    private int obtenerNivelDeEstresActual() {
        return 13; // Simulación: estrés actual en 65%
    }

    private int obtenerNivelDeEstresAnterior() {
        return 100; // Simulación: estrés anterior en 30%
    }
    private void actualizarBarraEstres(ProgressBar progressBar, ImageView stressIcon, int stressLevel) {
        // Asignar el progreso de la barra
        progressBar.setProgress(stressLevel);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(340, 45);


        System.out.println(progressBar.getProgress() + ": PROGRESO");

        // Determinar el color de la barra y el ícono correspondiente
        Drawable progressDrawable;
        int iconResource;

        int stressWidth = stressLevel * 11;

        if (stressLevel >= 75) {
            if (stressLevel >=80){
               stressWidth = 874;
            }
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.red_progress);
            //iconResource = R.drawable.ic_angry_face;
        } else if (stressLevel >= 50) {
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.yellow_progress);
            //iconResource = R.drawable.ic_neutral_face;
        } else if (stressLevel >= 25) {
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.green_progress);
            //iconResource = R.drawable.ic_smile_face;
        } else {
            progressDrawable = ContextCompat.getDrawable(this, R.drawable.blue_progress);
            //iconResource = R.drawable.ic_app_logo;
        }

        params = new LinearLayout.LayoutParams(stressWidth, 90);
        progressBar.setLayoutParams(params);

        // Aplicar los cambios
        progressBar.setProgressDrawable(progressDrawable);
        iconResource = R.drawable.logo_definitivo;
        stressIcon.setImageResource(iconResource);
    }
}