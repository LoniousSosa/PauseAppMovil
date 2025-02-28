package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(view -> {
            //Comprobación que el usuario no exista, si el nombre de usuario existe Toast

            //If true, creación de usuario y ->
            Intent intent = new Intent(RegisterActivity.this,LobbyActivity.class);
            startActivity(intent);
        });
    }
}