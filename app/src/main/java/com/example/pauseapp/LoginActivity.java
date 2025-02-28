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

public class LoginActivity extends AppCompatActivity {

    Button loginButton,registerSuggestionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        registerSuggestionButton = findViewById(R.id.registerSuggestion);

        loginButton.setOnClickListener(view -> {

            //Aqui irá la autentificación con Toast si es incorrecta

            //if true ->
            Intent intent = new Intent(LoginActivity.this,LobbyActivity.class);
            startActivity(intent);
        });

        //Esta no lo necesita, ya que solo tira a la pantalla de registro
        registerSuggestionButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
    }
}