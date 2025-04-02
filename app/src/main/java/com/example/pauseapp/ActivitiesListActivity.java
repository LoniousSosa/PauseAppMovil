package com.example.pauseapp;

import android.os.Bundle;

public class ActivitiesListActivity extends MenuFunction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_list);
        setupNavigation();

    }
}