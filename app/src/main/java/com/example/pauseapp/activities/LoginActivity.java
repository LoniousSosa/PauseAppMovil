package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.LoginRequest;
import com.example.pauseapp.model.LoginResponse;
import com.auth0.android.jwt.JWT;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton, registerSuggestionButton;
    private EditText mailEditText, passwordEditText;
    private boolean isPassword = false;
    private ImageView googleIcon, otherIcon;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleIcon = findViewById(R.id.googleIcon);
        otherIcon = findViewById(R.id.otherIcon);

        loginButton = findViewById(R.id.logToLobby);
        mailEditText = findViewById(R.id.mailEdit);
        registerSuggestionButton = findViewById(R.id.registerSuggestion);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        passwordEditText = findViewById(R.id.passwordEdit);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        togglePasswordVisibility.setOnClickListener(view -> passwordVisibilityCheck(togglePasswordVisibility));

        loginButton.setOnClickListener(view -> authenticateUser());

        registerSuggestionButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        otherIcon.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
            startActivity(intent);
        });
    }

    private void authenticateUser() {
        String email = mailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email,password);

        authApiService.loginUser(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
                    String token = response.body().getToken();
                    saveToken(token);
                    Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error de conexión", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToken(String token) {
        try {
            JWT jwt = new JWT(token);
            int userId = jwt.getClaim("userId").asInt();  // Ajusta el nombre del claim según tu backend

            SharedPreferences sharedPreferences = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("user_id", userId);
            editor.putString("auth_token", token);  // Guarda también el token para futuras llamadas
            editor.apply();

            Log.d("LoginActivity", "Usuario ID guardado: " + userId);
        } catch (Exception e) {
            Log.e("LoginActivity", "Error al decodificar el token", e);
        }
    }


    private void passwordVisibilityCheck(ImageView imageView) {
        if (isPassword) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.mostrar_contra);
            isPassword = false;
        } else {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.no_mostrar_contra_crop);
            isPassword = true;
        }
        passwordEditText.setTypeface(ResourcesCompat.getFont(this, R.font.feather_bold));
        passwordEditText.setSelection(passwordEditText.getText().length());
    }
}