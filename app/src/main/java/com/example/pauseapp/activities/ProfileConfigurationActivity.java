package com.example.pauseapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileConfigurationActivity extends AppCompatActivity {

    Button okButton, exitButtonNoti, changeStessButton, borrarCuenta;
    private static final String PREFS_NAME = "ProfilePrefs";
    EditText userNameEdit,passwordEdit,mailEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_configuration);
        okButton = findViewById(R.id.okButton);
        exitButtonNoti = findViewById(R.id.exitButton);
        userNameEdit = findViewById(R.id.userNameEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        mailEdit = findViewById(R.id.mailEdit);
        changeStessButton = findViewById(R.id.changeStressButton);
        borrarCuenta = findViewById(R.id.borrarCuenta);

        loadPreferences();

        okButton.setOnClickListener(view -> {
            savePreferences();
            Toast.makeText(this, "Configuración actualizada con éxito", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finish();
        });

        exitButtonNoti.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle("Salir sin guardar")
                        .setMessage("¿Estás seguro de que quieres salir? Los cambios no se guardarán.")
                        .setPositiveButton("Salir", (dialog, which) -> finish())
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                        .show());

        borrarCuenta.setOnClickListener(view -> {
            mostrarDialogoConfirmacion();
        });

        changeStessButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileConfigurationActivity.this,TestActivity.class);
            startActivity(intent);
        });

    }

    private void savePreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nombre usuario", userNameEdit.getText().toString());
        editor.putString("password", passwordEdit.getText().toString());
        editor.putString("mail", mailEdit.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        userNameEdit.setText(prefs.getString("nombre usuario"," "));
        passwordEdit.setText(prefs.getString("password"," "));
        mailEdit.setText(prefs.getString("mail"," "));
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar cuenta");
        builder.setMessage("¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");

        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.setPositiveButton("Eliminar", (dialog, which) -> eliminarCuenta());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
    private void eliminarCuenta() {
        // Aquí iría la lógica para borrar la cuenta (ejemplo: llamada a API, eliminar datos locales, etc.)
        // Por ahora, solo mostramos un mensaje de confirmación.
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(this);
        confirmDialog.setTitle("Cuenta eliminada");
        confirmDialog.setMessage("Tu cuenta ha sido eliminada con éxito.");
        confirmDialog.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(ProfileConfigurationActivity.this,MainActivity.class);
            startActivity(intent);
        });

        confirmDialog.create().show();
    }
     **/

    private void eliminarCuenta() {
        new AlertDialog.Builder(this)
                .setTitle("¿Eliminar cuenta?")
                .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí", (dialog, which) -> {
                    String token = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).getString("auth_token", "");
                    int userId = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).getInt("user_id", -1);
                    AuthApiService apiService = RetrofitClient.getClient().create(AuthApiService.class);

                    Call<Void> call = apiService.deleteUser(userId, token);

                    call.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).edit().clear().apply();

                                new AlertDialog.Builder(ProfileConfigurationActivity.this)
                                        .setTitle("Cuenta eliminada")
                                        .setMessage("Tu cuenta ha sido eliminada con éxito.")
                                        .setPositiveButton("OK", (dialog2, which2) -> {
                                            dialog2.dismiss();
                                            Intent intent = new Intent(ProfileConfigurationActivity.this, MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }).show();
                            } else {
                                Toast.makeText(ProfileConfigurationActivity.this, "No se pudo eliminar la cuenta", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(ProfileConfigurationActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

}