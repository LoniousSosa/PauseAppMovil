package com.example.pauseapp.model;

import com.google.gson.annotations.SerializedName;

public class StressLevelResponse {
    private Long id;

    @SerializedName("level")
    private float level;
    @SerializedName("date")
    private String date;

    public Long getId() { return id; }
    public float getLevel() { return level; }
    public String getDate() { return date; }
}

