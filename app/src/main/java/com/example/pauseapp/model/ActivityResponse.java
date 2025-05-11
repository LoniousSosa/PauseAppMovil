package com.example.pauseapp.model;

import com.google.gson.annotations.SerializedName;

public class ActivityResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isPremium;

    @SerializedName("type")
    private ActivityTypeResponse type;

    @SerializedName("thumbnailURL")
    private String thumbnailUrl;

    @SerializedName("media")
    private MediaResponse media;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ActivityTypeResponse getType() { return type; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public MediaResponse getMedia() { return media; }
    public Boolean getIsPremium() { return isPremium; }

}

