package com.example.pauseapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class PracticeActivity extends MenuFunction {

    private TextView timerText;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        setupNavigation();

        timerText = findViewById(R.id.timerText);
        Button startButton = findViewById(R.id.startButton);
        Button posponeButton = findViewById(R.id.posponeButton);
        FrameLayout mediaContainer = findViewById(R.id.mediaContainer);

        /**
         * Aqui irá la imagen gracias al token
         */

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.actividad4_2);
        mediaContainer.addView(imageView);

        /**
         * Si no hay video y solo es imagen, se empezará pulsando el boton
         * Si es con un video, la visbilidad del boton desaparecerá y el timer
         * empezará una vez el video inicie
         * El tiempo del timer por default será un minuto pero dependerá
         * de la activity
         */

        startButton.setOnClickListener(v -> startTimer());

        // Posponer temporizador
        posponeButton.setOnClickListener(v -> {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
        });
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
            }
        }.start();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }
}