package com.example.pauseapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfigurationActivity extends BaseActivity {

    Button okButton,exitButtonConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        okButton = findViewById(R.id.okButton);
        exitButtonConfiguration = findViewById(R.id.exitButtonConfiguration);

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
    }
}