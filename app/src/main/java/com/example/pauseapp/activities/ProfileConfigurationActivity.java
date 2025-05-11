package com.example.pauseapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.UserUpdateRequest;
import com.example.pauseapp.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileConfigurationActivity extends AppCompatActivity {

    private Button okButton, exitButtonNoti, changeProfileButton, borrarCuenta;
    private EditText userNameEdit, passwordEdit, repeatPasswordEdit;
    private static final String PREFS_NAME = "ProfilePrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_configuration);

        okButton            = findViewById(R.id.okButton);
        exitButtonNoti      = findViewById(R.id.exitButton);
        userNameEdit        = findViewById(R.id.userNameEdit);
        passwordEdit        = findViewById(R.id.passwordEdit);
        repeatPasswordEdit  = findViewById(R.id.repeatEdit);
        changeProfileButton = findViewById(R.id.changeProfile);
        borrarCuenta        = findViewById(R.id.borrarCuenta);

        loadPreferences();

        okButton.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle("Guardar cambios locales")
                        .setMessage("¿Deseas guardar el nombre de usuario y la contraseña localmente?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            savePreferences();
                            Toast.makeText(this, "Configuración guardada localmente", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show()
        );

        exitButtonNoti.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle("Salir sin guardar")
                        .setMessage("¿Estás seguro de que quieres salir? Los cambios no se guardarán.")
                        .setPositiveButton("Salir", (d, w) -> finish())
                        .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                        .show()
        );

        changeProfileButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Actualizar perfil")
                        .setMessage("¿Deseas actualizar tu nombre de usuario y contraseña en el servidor?")
                        .setPositiveButton("Sí", (dialog, which) -> updateProfile())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show()
        );

        borrarCuenta.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar cuenta")
                        .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                        .setPositiveButton("Sí", (dialog, which) -> deleteAccount())
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                        .show()
        );
    }

    private void updateProfile() {
        String newUsername = userNameEdit.getText().toString().trim();
        String newPassword = passwordEdit.getText().toString();
        String confirmPass = repeatPasswordEdit.getText().toString();

        if (newUsername.isEmpty()) {
            userNameEdit.setError("El nombre de usuario no puede estar vacío");
            return;
        }
        if (!newPassword.isEmpty() && !newPassword.equals(confirmPass)) {
            repeatPasswordEdit.setError("Las contraseñas no coinciden");
            return;
        }

        SharedPreferences pausePrefs = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
        String token = pausePrefs.getString("auth_token", "");
        long userId = pausePrefs.getLong("user_id", -1);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setName(newUsername);
        if (!newPassword.isEmpty()) {
            request.setPassword(newPassword);
        }

        AuthApiService apiService = RetrofitClient.getClient().create(AuthApiService.class);
        apiService.patchUser(userId, request, "Bearer " + token)
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            savePreferences();
                            Toast.makeText(ProfileConfigurationActivity.this,
                                    "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e("ProfileConfig", "Error update: " + response.code());
                            Toast.makeText(ProfileConfigurationActivity.this,
                                    "Error al actualizar perfil (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Log.e("ProfileConfig", "onFailure", t);
                        Toast.makeText(ProfileConfigurationActivity.this,
                                "Fallo de red al actualizar perfil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteAccount() {
        SharedPreferences pausePrefs = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
        String token = pausePrefs.getString("auth_token", "");
        long userId = pausePrefs.getLong("user_id", -1);

        AuthApiService apiService = RetrofitClient.getClient().create(AuthApiService.class);
        apiService.deleteUser(userId, "Bearer " + token)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            pausePrefs.edit().clear().apply();
                            new AlertDialog.Builder(ProfileConfigurationActivity.this)
                                    .setTitle("Cuenta eliminada")
                                    .setMessage("Tu cuenta ha sido eliminada con éxito.")
                                    .setPositiveButton("OK", (d, w) -> {
                                        Intent intent = new Intent(ProfileConfigurationActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    })
                                    .show();
                        } else {
                            Toast.makeText(ProfileConfigurationActivity.this,
                                    "No se pudo eliminar la cuenta", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ProfileConfigurationActivity.this,
                                "Error de red al eliminar cuenta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre usuario", userNameEdit.getText().toString().trim());
        editor.putString("password", passwordEdit.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        userNameEdit.setText(prefs.getString("nombre usuario", ""));
        passwordEdit.setText(prefs.getString("password", ""));
        repeatPasswordEdit.setText(prefs.getString("password", ""));
    }
}

