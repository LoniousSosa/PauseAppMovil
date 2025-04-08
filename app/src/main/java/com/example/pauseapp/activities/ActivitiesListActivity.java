package com.example.pauseapp.activities;

import android.os.Bundle;

import com.example.pauseapp.R;

public class ActivitiesListActivity extends MenuFunction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);
        setupNavigation();

    }
}