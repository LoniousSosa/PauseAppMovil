package com.example.pauseapp.model;

import com.google.gson.annotations.SerializedName;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String name;
    private String email;

    @SerializedName("subscription")
    private Boolean isSubscribed;

    @SerializedName("initialStressLevel")
    private String initialStressLevel;

    @SerializedName("actualStressLevel")
    private String actualStressLevel;

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

    public String getInitialStressLevel() {
        return initialStressLevel;
    }

    public String getActualStressLevel() {
        return actualStressLevel;
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
}

