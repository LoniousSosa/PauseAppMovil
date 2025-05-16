package com.example.pauseapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.UserResponse;
import com.example.pauseapp.model.UserUpdateRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Map<Button, LinearLayout> paymentForms;
    private LinearLayout currentVisibleForm;
    private Button buyButton;
    private AuthApiService apiService;
    private String token;
    private long userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firs_step_shop);

        SharedPreferences prefs = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE);
        token = prefs.getString("auth_token", null);
        userId = prefs.getLong("user_id", -1);
        apiService = RetrofitClient.getClient().create(AuthApiService.class);

        buyButton = findViewById(R.id.buyButton);
        paymentForms = new HashMap<>();
        paymentForms.put(findViewById(R.id.visaButton), findViewById(R.id.formVisa));
        paymentForms.put(findViewById(R.id.buttonPaypal), findViewById(R.id.formPaypal));
        paymentForms.put(findViewById(R.id.buttonBizum), findViewById(R.id.formBizum));
        paymentForms.put(findViewById(R.id.buttonCuenta), findViewById(R.id.formCuentaBancaria));

        for (Map.Entry<Button, LinearLayout> entry : paymentForms.entrySet()) {
            entry.getKey().setOnClickListener(v -> toggleFormVisibility(entry.getValue()));
        }

        buyButton.setOnClickListener(v -> onBuyClicked());
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Deshabilitar botón atrás para no regresar a pantalla de pago
    }

    private void toggleFormVisibility(LinearLayout selectedForm) {
        for (LinearLayout form : paymentForms.values()) {
            if (form != selectedForm) clearForm(form);
        }
        if (currentVisibleForm != null) {
            currentVisibleForm.setVisibility(View.GONE);
        }
        if (currentVisibleForm == selectedForm) {
            currentVisibleForm = null;
        } else {
            selectedForm.setVisibility(View.VISIBLE);
            currentVisibleForm = selectedForm;
        }
    }

    private void clearForm(LinearLayout form) {
        for (int i = 0; i < form.getChildCount(); i++) {
            View child = form.getChildAt(i);
            if (child instanceof EditText) {
                ((EditText) child).setText("");
            }
        }
    }

    private void onBuyClicked() {
        if (currentVisibleForm == null) {
            Toast.makeText(this, "Seleccione un método de pago", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < currentVisibleForm.getChildCount(); i++) {
            View child = currentVisibleForm.getChildAt(i);
            if (child instanceof EditText) {
                if (((EditText) child).getText().toString().trim().isEmpty()) {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        UserUpdateRequest updateUser = new UserUpdateRequest();
        updateUser.setSubscription(true);

        Call<UserResponse> call = apiService.patchUser(userId, updateUser, "Bearer " + token);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("PauseAppPrefs", MODE_PRIVATE).edit();
                    editor.putBoolean("subscription", true);
                    editor.apply();

                    Toast.makeText(PaymentActivity.this,
                            "Su suscripción se ha realizado con éxito, se renovará cada mes de forma automática",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PaymentActivity.this, LobbyActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    int code = response.code();
                    String err = "sin cuerpo";
                    try {
                        if (response.errorBody() != null)
                            err = response.errorBody().string();
                    } catch (IOException ignored) { }
                    Log.e("PaymentActivity", "PATCH suscripción → Error " + code + ": " + err);
                    Toast.makeText(PaymentActivity.this,
                            "Error " + code + ": " + err,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(PaymentActivity.this,
                        "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}