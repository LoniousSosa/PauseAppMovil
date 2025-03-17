package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    EditText userNameEdit,emailEdit,passwordEditText,repeatEdit;
    ImageView togglePasswordVisibility, toggleRepeatVisibility;
    View.OnClickListener clickListener;

    ArrayList<String> usuariosRegistrados;
    private boolean isPassword = false;


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

        creacionListener();

        togglePasswordVisibility.setOnClickListener(clickListener);
        toggleRepeatVisibility.setOnClickListener(clickListener);

        registerButton.setOnClickListener(view -> {
            //Comprobación que el usuario no exista, si el nombre de usuario existe Toast

            if (!comprobacionUsuarios(userNameEdit.getText().toString())){
                return;
            }

            if (!comprobacionEmail(emailEdit.getText().toString().trim())){
                return;
            }

            //Una vez no existe el usuario, comprobarContraseñas
            if (!comprobacionContras()) {
                return;
            }

            //Creación de usuario y ->

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
}