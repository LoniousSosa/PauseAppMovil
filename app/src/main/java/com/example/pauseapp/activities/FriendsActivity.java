package com.example.pauseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.pauseapp.R;

public class FriendsActivity extends MenuFunction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        setupNavigationNoBottom();
        /**
         * Código temporal para probar el barchart
         */

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(FriendsActivity.this, FriendProfileActivity.class);
            startActivity(intent);
        });
    }
}