package com.example.pauseapp.model;

public class User {
    private long id;
    private String name;
    private String email;
    private String password;
    private Boolean subscription = false;
    private float initialStressLevel;
    private float actualStressLevel;
    private Integer streakDays = 0;
    private Integer completedActivities = 0;
    private Integer alertInterval = 24;

    public User() { }

    public User(String username, String email, String password) {
        this.name = username;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public String getUsername() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

}
