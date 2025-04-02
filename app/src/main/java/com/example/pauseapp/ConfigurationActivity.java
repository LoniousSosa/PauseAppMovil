package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ConfigurationActivity extends MenuFunction {

    Button okButton,exitButtonConfiguration,notiButton,preferenciasButton,perfilButton,privacidadButton;

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
    }
}