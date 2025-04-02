package com.example.pauseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PresentationActivity extends MenuFunction {

    Button startButton, infoButton;
    TextView activityTitle, description;
    ImageView imageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        startButton = findViewById(R.id.startButton);
        infoButton = findViewById(R.id.infoButton);
        setupNavigation();

        activityTitle = findViewById(R.id.activityTitle);
        description = findViewById(R.id.descripctionActivity);
        imageActivity = findViewById(R.id.imageActivity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String activityName = extras.getString("NAME", "Actividad Desconocida");
            int imageResource = extras.getInt("IMAGE", R.drawable.actividad4_2);

            activityTitle.setText(activityName);
            imageActivity.setImageResource(imageResource);
        }

        startButton.setOnClickListener(view -> {
            Intent intent = new Intent(PresentationActivity.this,PracticeActivity.class);
            startActivity(intent);
        });
        infoButton.setOnClickListener(view -> {
            Intent intent = new Intent(PresentationActivity.this,ActivityInfoActivity.class);
            startActivity(intent);
        });
    }
}