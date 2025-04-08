package com.example.pauseapp.entidad;

public class User {
    private String name;
    private String email;
    private String password;
    private Boolean subscription = false;
    private String initialStressLevel;
    private String actualStressLevel = this.initialStressLevel;
    private Integer streakDays = 0;
    private Integer completedActivities = 0;
    private Integer alertInterval = 24;


    public User(String username, String email, String password) {
        this.name = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

}
