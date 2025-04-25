package com.example.pauseapp.model;

public class ActivityRecordCreateRequest {
    private Long activityId;
    private String time;
    private Boolean status;

    public ActivityRecordCreateRequest(Long activityId, String time, Boolean status) {
        this.activityId = activityId;
        this.time = time;
        this.status = status;
    }
    public Long getActivityId() {
        return activityId;
    }
    public String getTime() {
        return time;
    }
    public Boolean getStatus() {
        return status;
    }
}

