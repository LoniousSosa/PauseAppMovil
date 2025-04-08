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

        // Botón "Cancelar"
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        // Botón "Eliminar"
        builder.setPositiveButton("Eliminar", (dialog, which) -> eliminarCuenta());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

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
}