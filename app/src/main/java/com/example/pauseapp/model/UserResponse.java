package com.example.pauseapp.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String name;
    private String email;

    @SerializedName("subscription")
    private Boolean isSubscribed;

    @SerializedName("initialStressLevel")
    private float initialStressLevel;

    @SerializedName("actualStressLevel")
    private float actualStressLevel;

    @SerializedName("streakDays")
    private Integer streakDays;

    @SerializedName("completedActivities")
    private Integer completedActivities;

    @SerializedName("alertInterval")
    private Integer alertInterval;

    @SerializedName("roles")
    private Set<String> roles;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getIsSubscribed() {
        return isSubscribed;
    }


    public Integer getStreakDays() {
        return streakDays;
    }

    public Integer getCompletedActivities() {
        return completedActivities;
    }

    public Integer getAlertInterval() {
        return alertInterval;
    }

    public Set<String> getRoles() {
        return roles;
    }

    @SerializedName("stressLevels")
    private List<StressLevelResponse> stressLevels;

    public List<StressLevelResponse> getStressLevels() {
        return stressLevels;
    }
}