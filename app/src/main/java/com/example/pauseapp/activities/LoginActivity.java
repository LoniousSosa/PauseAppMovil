package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.LoginRequest;
import com.example.pauseapp.model.LoginResponse;
import com.example.pauseapp.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFS_NAME    = "PauseAppPrefs";
    private static final String KEY_TOKEN     = "auth_token";
    private static final String KEY_USER_ID   = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_SUBSCRIPTION = "subscription";


    private EditText mailEditText, passwordEditText;
    private AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mailEditText     = findViewById(R.id.mailEdit);
        passwordEditText = findViewById(R.id.passwordEdit);
        authApiService   = RetrofitClient.getClient().create(AuthApiService.class);

        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        EditText passwordEditText = findViewById(R.id.passwordEdit);

        togglePasswordVisibility.setOnClickListener(v -> {
            toggleListener(togglePasswordVisibility, passwordEditText);
        });

        findViewById(R.id.logToLobby).setOnClickListener(view -> authenticateUser());
        findViewById(R.id.registerSuggestion).setOnClickListener(view ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void authenticateUser() {
        String email    = mailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authApiService.loginUser(new LoginRequest(email, password))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) {
                            Toast.makeText(LoginActivity.this,
                                    "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String token = resp.body().getToken();
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        prefs.edit().putString(KEY_TOKEN, token).apply();

                        authApiService.getUser("Bearer " + token)
                                .enqueue(new Callback<>() {
                                    @Override
                                    public void onResponse(Call<UserResponse> call, Response<UserResponse> r2) {
                                        if (r2.isSuccessful() && r2.body() != null) {
                                            UserResponse u = r2.body();
                                            prefs.edit()
                                                    .putLong(KEY_USER_ID, u.getId())
                                                    .putString(KEY_USER_NAME, u.getUsername())
                                                        .putBoolean(KEY_SUBSCRIPTION, u.getIsSubscribed() != null
                                                                && u.getIsSubscribed())
                                                    .apply();

                                            boolean filtersDone = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE)
                                                    .getBoolean("initial_filters_done", false);


                                            Intent next;

                                            /**
                                             * Si los filtros no funciona, comentar el if-else
                                             */

                                            if (!filtersDone) {
                                                next = new Intent(LoginActivity.this, InitialFiltersActivity.class);
                                            } else {
                                                next = new Intent(LoginActivity.this, LobbyActivity.class);
                                            }
                                            next = new Intent(LoginActivity.this, LobbyActivity.class);
                                            startActivity(next);
                                            finish();

                                        } else {
                                            Toast.makeText(LoginActivity.this,
                                                    "No se pudo obtener datos de usuario", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserResponse> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this,
                                                "Error de conexión al cargar perfil", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,
                                "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void toggleListener(ImageView togglePasswordVisibility, EditText passwordEditText){
        boolean isVisible = (passwordEditText.getInputType()
                == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD));
        if (isVisible) {
            passwordEditText.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.mostrar_contra);
        } else {
            passwordEditText.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            togglePasswordVisibility.setImageResource(R.drawable.no_mostrar_contra_crop);
        }
        passwordEditText.setSelection(passwordEditText.length());
    }
}