package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        ImageView logo = findViewById(R.id.logo);
        TextView title = findViewById(R.id.firstTitle);

        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);


        logo.startAnimation(bounceAnimation);
        title.startAnimation(fadeInAnimation);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(FlashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, 3500);
    }
}