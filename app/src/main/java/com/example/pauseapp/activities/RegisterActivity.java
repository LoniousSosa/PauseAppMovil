package com.example.pauseapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.pauseapp.R;
import com.example.pauseapp.model.User;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.LoginRequest;
import com.example.pauseapp.model.LoginResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ID   = "user_id";

    private static final String KEY_SUBSCRIPTION = "subscription";

    EditText userNameEdit, emailEdit, passwordEditText, repeatEdit;
    ImageView togglePasswordVisibility, toggleRepeatVisibility;
    View.OnClickListener clickListener;
    AuthApiService authApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        userNameEdit = findViewById(R.id.userNameEdit);
        emailEdit = findViewById(R.id.mailEdit);
        passwordEditText = findViewById(R.id.passwordEdit);
        repeatEdit = findViewById(R.id.repeatEdit);
        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        toggleRepeatVisibility = findViewById(R.id.toggleRepeatVisibility);
        Button registerButton = findViewById(R.id.registerButton);

        creacionListener();
        togglePasswordVisibility.setOnClickListener(clickListener);
        toggleRepeatVisibility.setOnClickListener(clickListener);

        registerButton.setOnClickListener(view -> attemptRegister());
    }

    private void attemptRegister() {
        String username = userNameEdit.getText().toString().trim();
        String email = emailEdit.getText().toString().trim();
        String pass = passwordEditText.getText().toString();
        String repeat = repeatEdit.getText().toString();

        if (username.isEmpty()) {
            Toast.makeText(this, "El nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!comprobacionEmail(email)) return;
        if (!comprobacionContras(pass, repeat)) return;

        authApiService.checkUsername(username).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 409) {
                    Toast.makeText(RegisterActivity.this,
                            "El nombre de usuario ya está registrado",
                            Toast.LENGTH_SHORT).show();
                } else if (response.isSuccessful()) {
                    registrarUsuario(username, email, pass);
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Error comprobando usuario",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Error de conexión al comprobar usuario",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean comprobacionEmail(String email) {
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (email.isEmpty()) {
            Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean comprobacionContras(String pass, String repeat) {
        if (pass.isEmpty() || repeat.isEmpty()) {
            Toast.makeText(this, "Alguna de las contraseñas está vacía",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!pass.equals(repeat)) {
            Toast.makeText(this, "Las contraseñas no coinciden",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registrarUsuario(String username, String email, String password) {
        User user = new User(username, email, password);
        authApiService.registerUser(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    getTokenAfterRegister(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTokenAfterRegister(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        authApiService.loginUser(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivity.this,
                            "Token guardado correctamente", Toast.LENGTH_SHORT).show();
                    String token = response.body().getToken();
                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    prefs.edit()
                            .putString("auth_token", token)
                            .apply();

                    // Decidir siguiente pantalla: filtros iniciales o lobby
                    boolean filtersDone = prefs.getBoolean("initial_filters_done", false);
                    Intent intent;
                    if (!filtersDone) {
                        intent = new Intent(RegisterActivity.this, InitialFiltersActivity.class);
                    } else {
                        intent = new Intent(RegisterActivity.this, LobbyActivity.class);
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null
                                ? response.errorBody().string()
                                : "–sin cuerpo–";
                        Log.e("RegisterActivity", "Login tras registro falló. code="
                                + response.code()
                                + " errorBody=" + errorBody);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(RegisterActivity.this,
                            "Error al iniciar sesión tras el registro (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Error de conexión tras registro",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void creacionListener() {
        clickListener = view -> {
            boolean showing = togglePasswordVisibility.isSelected();
            togglePasswordVisibility.setSelected(!showing);
            toggleRepeatVisibility.setSelected(!showing);

            int inputType = showing
                    ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    : (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            passwordEditText.setInputType(inputType);
            repeatEdit.setInputType(inputType);

            passwordEditText.setTypeface(ResourcesCompat.getFont(this, R.font.feather_bold));
            passwordEditText.setSelection(passwordEditText.getText().length());
            repeatEdit.setTypeface(ResourcesCompat.getFont(this, R.font.feather_bold));
            repeatEdit.setSelection(repeatEdit.getText().length());

            togglePasswordVisibility.setImageResource(
                    showing ? R.drawable.mostrar_contra : R.drawable.no_mostrar_contra_crop);
            toggleRepeatVisibility.setImageResource(
                    showing ? R.drawable.mostrar_contra : R.drawable.no_mostrar_contra_crop);
        };
    }
}