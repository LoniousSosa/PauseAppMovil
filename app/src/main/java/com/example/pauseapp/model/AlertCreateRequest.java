package com.example.pauseapp.model;

public class AlertCreateRequest {
    private String title;
    private String message;

    public AlertCreateRequest(String title, String message) {
        this.title   = title;
        this.message = message;
    }
    public String getTitle()   { return title; }
    public String getMessage() { return message; }
}
