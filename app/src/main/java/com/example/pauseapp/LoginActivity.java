package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    Button loginButton,registerSuggestionButton;
    EditText nombreEditText,passwordEditText;

    private boolean isPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.logToLobby);
        nombreEditText = findViewById(R.id.userNameEdit);
        registerSuggestionButton = findViewById(R.id.registerSuggestion);
        ImageView togglePasswordVisibility = findViewById(R.id.togglePasswordVisibility);
        passwordEditText = findViewById(R.id.passwordEdit);
        
        togglePasswordVisibility.setOnClickListener(view -> {
            passwordVisibilityCheck(togglePasswordVisibility);
        });

        loginButton.setOnClickListener(view -> {

            //Aqui irá la autentificación con Toast si es incorrecta
            if (!logInCheck(nombreEditText,passwordEditText)){
                return;
            }
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

    private boolean logInCheck(EditText username, EditText passwrd){
        /**
         * Comprobación en la Database del usuario y la contraseña
         *
         */
        String usernameString = username.getText().toString();
        String passwrdString = passwrd.getText().toString();
        if(true){
            return true;
        }
        return false;
    }

    private void passwordVisibilityCheck(ImageView imageView){
        if (isPassword){
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imageView.setImageResource(R.drawable.mostrar_contra);
            isPassword = false;
        } else {
            isPassword = true;
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imageView.setImageResource(R.drawable.no_mostrar_contra_crop);
        }
        passwordEditText.setTypeface(ResourcesCompat.getFont(this, R.font.feather_bold));
        passwordEditText.setSelection(passwordEditText.getText().length());
    }
}