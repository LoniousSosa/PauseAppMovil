package com.example.pauseapp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.AlertCreateRequest;
import com.example.pauseapp.model.AlertResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewAlertActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "PauseAppPrefs";

    private EditText titleEdit, messageEdit;
    private Button createButton;
    private AuthApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alert);

        titleEdit   = findViewById(R.id.alertTitleEdit);
        messageEdit = findViewById(R.id.alertMessageEdit);
        createButton= findViewById(R.id.createAlertButton);

        // Retrofit + token
        apiService = RetrofitClient.getClient().create(AuthApiService.class);
        authToken  = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString("auth_token", "");

        createButton.setOnClickListener(view -> attemptCreateAlert());
    }

    private void attemptCreateAlert() {
        String title   = titleEdit.getText().toString().trim();
        String message = messageEdit.getText().toString().trim();

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Rellena ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertCreateRequest req = new AlertCreateRequest(title, message);
        apiService.createAlert(req, "Bearer " + authToken)
                .enqueue(new Callback<AlertResponse>() {
                    @Override
                    public void onResponse(Call<AlertResponse> call, Response<AlertResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            Toast.makeText(NewAlertActivity.this,
                                    "Alerta creada: " + resp.body().getTitle(),
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(NewAlertActivity.this,
                                    "Error creando alerta", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<AlertResponse> call, Throwable t) {
                        Toast.makeText(NewAlertActivity.this,
                                "Falló la conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
