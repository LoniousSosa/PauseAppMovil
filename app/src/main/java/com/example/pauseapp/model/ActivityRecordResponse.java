package com.example.pauseapp.model;

public class ActivityRecordResponse {
    private Long id;
    private String time;     // ISO 8601
    private Boolean status;
    private User user;     // opcional
    private ActivityResponse activity; // opcional

    public Long getId() {
        return id;
    }
    public String getTime() {
        return time;
    }
    public Boolean getStatus() {
        return status;
    }
    public User getUser() {
        return user;
    }
    public ActivityResponse getActivity() {
        return activity;
    }
}

