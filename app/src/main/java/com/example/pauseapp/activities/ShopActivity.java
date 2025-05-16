package com.example.pauseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pauseapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShopActivity extends MenuFunction {

    Button buyButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_item_background);

        setupNavigation();
        buyButton = findViewById(R.id.buyButton);

        buyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,PaymentActivity.class);
            startActivity(intent);
        });
    }
}