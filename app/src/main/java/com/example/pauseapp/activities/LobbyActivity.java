package com.example.pauseapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.pauseapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LobbyActivity extends MenuFunction {
    ImageButton primeraActivity, segundaActivity, terceraActivity, cuartaActivity, quintaActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemBackgroundResource(R.drawable.bottom_nav_item_background);
        primeraActivity = findViewById(R.id.primeraActivity);
        segundaActivity = findViewById(R.id.segundaActivity);
        terceraActivity = findViewById(R.id.terceraActivity);
        cuartaActivity = findViewById(R.id.cuartaActivity);
        quintaActivity = findViewById(R.id.quintaActivity);

        setButtonData(primeraActivity, "Meditación", R.drawable.actividad_cuatro);
        setButtonData(segundaActivity, "Relajación", R.drawable.actividad_uno);
        setButtonData(terceraActivity, "Respiración", R.drawable.actividad_cuatro);
        setButtonData(cuartaActivity, "Mindfullness", R.drawable.actividad_uno);
        setButtonData(quintaActivity, "Enfoque", R.drawable.actividad_cuatro);

        primeraActivity.setOnClickListener(this::getActivityInfo);
        segundaActivity.setOnClickListener(this::getActivityInfo);
        terceraActivity.setOnClickListener(this::getActivityInfo);
        cuartaActivity.setOnClickListener(this::getActivityInfo);
        quintaActivity.setOnClickListener(this::getActivityInfo);
        setupNavigation();
    }
    private void setButtonData(ImageButton button, String activityName, int imageResource) {
        Bundle data = new Bundle();
        data.putString("NAME", activityName);
        data.putInt("IMAGE", imageResource);
        button.setTag(data);
    }

    public void getActivityInfo(View view) {
        ImageButton clickedButton = (ImageButton) view;
        Bundle data = (Bundle) clickedButton.getTag();
        if (data != null) {
            Intent intent = new Intent(this, PresentationActivity.class);
            intent.putExtras(data);
            startActivity(intent);
        }
    }


}