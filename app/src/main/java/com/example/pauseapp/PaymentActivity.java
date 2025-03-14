package com.example.pauseapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private Map<Button, LinearLayout> paymentForms;
    private LinearLayout currentVisibleForm = null;

    /**
     * Una vez termine con la API, realizaré una tienda funcional
     */
    private Button buyButton;


    @SuppressLint("FindViewByIdCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firs_step_shop);

        buyButton = findViewById(R.id.buyButton);
        paymentForms = new HashMap<>();
        paymentForms.put(findViewById(R.id.visaButton), findViewById(R.id.formVisa));
        paymentForms.put(findViewById(R.id.buttonPaypal), findViewById(R.id.formPaypal));
        paymentForms.put(findViewById(R.id.buttonBizum), findViewById(R.id.formBizum));
        paymentForms.put(findViewById(R.id.buttonCuenta), findViewById(R.id.formCuentaBancaria));

        for (Map.Entry<Button,LinearLayout> entry:
             paymentForms.entrySet()) {
            entry.getKey().setOnClickListener(view -> {
                toggleFormVisibility(entry.getValue());
            });
        }
    }

    private void toggleFormVisibility(LinearLayout selectedForm) {
        if (currentVisibleForm != null) {
            currentVisibleForm.setVisibility(View.GONE);
        }
        if (currentVisibleForm != selectedForm) {
            selectedForm.setVisibility(View.VISIBLE);
            currentVisibleForm = selectedForm;
        } else {
            currentVisibleForm = null;
        }
    }

}