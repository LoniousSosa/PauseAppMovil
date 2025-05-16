package com.example.pauseapp.model;

/**
 * DTO para actualizar campos del usuario en el endpoint PATCH /user/{id}
 */
public class UserUpdateRequest {
    private String name;
    private String password;
    private String email;
    private Boolean subscription;
    private Integer streakDays;
    private Integer completedActivities;
    private Integer alertInterval;

    public UserUpdateRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getSubscription() {
        return subscription;
    }

    public void setSubscription(Boolean subscription) {
        this.subscription = subscription;
    }

    public Integer getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(Integer streakDays) {
        this.streakDays = streakDays;
    }

    public Integer getCompletedActivities() {
        return completedActivities;
    }

    public void setCompletedActivities(Integer completedActivities) {
        this.completedActivities = completedActivities;
    }

    public Integer getAlertInterval() {
        return alertInterval;
    }

    public void setAlertInterval(Integer alertInterval) {
        this.alertInterval = alertInterval;
    }
}

