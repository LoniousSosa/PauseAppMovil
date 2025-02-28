package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LobbyActivity extends AppCompatActivity {
    ImageButton menuLateral, profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_item_background);

        menuLateral = findViewById(R.id.menuLateral);
        profileButton = findViewById(R.id.profileButton);

        menuLateral.setOnClickListener(view -> {
            Intent intent = new Intent(LobbyActivity.this,ConfigurationActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(LobbyActivity.this,ProfileActivity.class);
            startActivity(intent);
        });
    }
}