package com.example.pauseapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pauseapp.fragments.PrivacyDialogFragment;
import com.example.pauseapp.R;

public class ConfigurationActivity extends MenuFunction {

    Button okButton,exitButtonConfiguration,notiButton,preferenciasButton,
            perfilButton,privacidadButton,centroAyudaButton, sugerenciasButton;
    final String BASE_URL = "34.206.229.191";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        okButton = findViewById(R.id.okButton);
        exitButtonConfiguration = findViewById(R.id.exitButtonConfiguration);
        notiButton = findViewById(R.id.notiButton);
        preferenciasButton = findViewById(R.id.preferenciasButton);
        perfilButton = findViewById(R.id.perfilButton);
        privacidadButton = findViewById(R.id.privacidadButton);
        centroAyudaButton    = findViewById(R.id.centroAyudaButton);
        sugerenciasButton     = findViewById(R.id.sugerenciasButton);
        TextView terminosTextView    = findViewById(R.id.terminos);

        okButton.setOnClickListener(view -> {
            Toast.makeText(this,"Configuración actualizada con exito",
                    Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finish();
        });
        exitButtonConfiguration.setOnClickListener(view -> finish());

        notiButton.setOnClickListener(view -> {
            Intent intent = new Intent(ConfigurationActivity.this,NotificationConfigActivity.class);
            startActivity(intent);
        });

        preferenciasButton.setOnClickListener(view -> {
            Intent intent = new Intent(ConfigurationActivity.this,PreferencesActivity.class);
            startActivity(intent);
        });

        perfilButton.setOnClickListener(view -> {
            Intent intent = new Intent(ConfigurationActivity.this, ProfileConfigurationActivity.class);
            startActivity(intent);
        });

        privacidadButton.setOnClickListener(view -> {
            PrivacyDialogFragment dialogFragment = new PrivacyDialogFragment();
            dialogFragment.show(getSupportFragmentManager(),"PrivacyDialog");
        });

        centroAyudaButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(BASE_URL + "/help"));
            startActivity(intent);
        });

        sugerenciasButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(BASE_URL + "/sugerencias"));
            startActivity(intent);
        });

        terminosTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(BASE_URL + "/terminos"));
            startActivity(intent);
        });
    }
}