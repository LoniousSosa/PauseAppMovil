package com.example.pauseapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class ProfileActivity extends MenuFunction {

    private ProgressBar stressBarCurrent, stressBarPrevious;
    private ImageView stressIconCurrent, stressIconPrevious;

    private int stressLevelCurrent,stressLevelPrevious;
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

         stressLevelCurrent = obtenerNivelDeEstresActual(); // 0-100
         stressLevelPrevious = obtenerNivelDeEstresAnterior(); // 0-100

        actualizarBarrasDeEstres();

        stressButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this,TestActivity.class);
            startActivity(intent);
        });
    }

    private int obtenerNivelDeEstresActual() {
        /**
         * Este stressLvl luego se pillará de la api
         */
        return TestActivity.getStressLvl(); // Simulación: estrés actual en 65%
    }

    private int obtenerNivelDeEstresAnterior() {
        return 100; // Simulación: estrés anterior en 30%
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarBarrasDeEstres();
    }

    private void actualizarBarrasDeEstres() {
        int stressLevelCurrent = obtenerNivelDeEstresActual();
        int stressLevelPrevious = obtenerNivelDeEstresAnterior();

        actualizarBarraEstres(stressBarCurrent, stressIconCurrent, stressLevelCurrent);
        actualizarBarraEstres(stressBarPrevious, stressIconPrevious, stressLevelPrevious);
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