package com.example.pauseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    EditText userNameEdit,emailEdit,passwordEditText,repeatEdit;
    ImageView togglePasswordVisibility, toggleRepeatVisibility;
    View.OnClickListener clickListener;

    ArrayList<String> usuariosRegistrados;
    private boolean isPassword = false;
    private AuthApiService authApiService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button registerButton = findViewById(R.id.registerButton);

        usuariosRegistrados = new ArrayList<>();


        passwordEditText = findViewById(R.id.passwordEdit);
        repeatEdit = findViewById(R.id.repeatEdit);
        userNameEdit = findViewById(R.id.userNameEdit);
        emailEdit = findViewById(R.id.mailEdit);


        togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        toggleRepeatVisibility = findViewById(R.id.toggleRepeatVisibility);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);

        creacionListener();

        togglePasswordVisibility.setOnClickListener(clickListener);
        toggleRepeatVisibility.setOnClickListener(clickListener);

        registerButton.setOnClickListener(view -> {

            if (!comprobacionUsuarios(userNameEdit.getText().toString())){
                return;
            }

            if (!comprobacionEmail(emailEdit.getText().toString().trim())){
                return;
            }

            if (!comprobacionContras()) {
                return;
            }

            String username = userNameEdit.getText().toString();
            String email = emailEdit.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String repeatPassword = repeatEdit.getText().toString();

            registrarUsuario(username, email, password);

            Toast.makeText(this, "Registro realizado", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(RegisterActivity.this, LobbyActivity.class);
            startActivity(intent);
        });
    }

    private boolean comprobacionUsuarios(String username) {

        for (int i = 0; i < 3; i++) {
            usuariosRegistrados.add("usuario"+i);
        }

        if (username.isEmpty()) {
            Toast.makeText(this, "El nombre de usuario no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (usuariosRegistrados.contains(username)) {
            Toast.makeText(this, "El nombre de usuario ya está registrado", Toast.LENGTH_SHORT).show();
            return false;
        }

        usuariosRegistrados.add(username);
        return true;
    }


    private boolean comprobacionEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

        if (email.isEmpty()) {
            Toast.makeText(this, "El correo no puede estar vacío", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!email.matches(emailPattern)) {
            Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private boolean comprobacionContras(){
        if (passwordEditText.getText().toString().isEmpty() ||
                repeatEdit.getText().toString().isEmpty()){
            Toast.makeText(this, "Alguna de las contraseñas está vacía",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passwordEditText.getText().toString().equals(repeatEdit.getText().toString())) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registrarUsuario(String username, String email, String password) {
        User user = new User(username, email, password);
        Call<Void> call = authApiService.registerUser(user);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_LONG).show();
                    // Aquí hacemos login automáticamente
                    getTokerAfterRegister(email, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Error en el registro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void  creacionListener(){
         clickListener = view -> {
            if (isPassword){
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                repeatEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

                togglePasswordVisibility.setImageResource(R.drawable.mostrar_contra);
                toggleRepeatVisibility.setImageResource(R.drawable.mostrar_contra);
                isPassword = false;
            } else {
                isPassword = true;
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                repeatEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

                togglePasswordVisibility.setImageResource(R.drawable.no_mostrar_contra_crop);
                toggleRepeatVisibility.setImageResource(R.drawable.no_mostrar_contra_crop);
            }
            passwordEditText.setTypeface(ResourcesCompat.getFont(this, R.font.feather_bold));
            passwordEditText.setSelection(passwordEditText.getText().length());
            repeatEdit.setTypeface(ResourcesCompat.getFont(this, R.font.feather_bold));
            repeatEdit.setSelection(repeatEdit.getText().length());
        };
    }

    private void getTokerAfterRegister(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        authApiService.loginUser(loginRequest).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();

                    getSharedPreferences("PauseAppPrefs", MODE_PRIVATE)
                            .edit()
                            .putString("user_token", token)
                            .apply();

                    Intent intent = new Intent(RegisterActivity.this, LobbyActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "Error al iniciar sesión tras el registro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error de conexión tras registro", Toast.LENGTH_SHORT).show();
            }
        });
    }


}