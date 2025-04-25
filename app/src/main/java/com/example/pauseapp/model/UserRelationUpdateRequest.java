// UserRelationUpdateRequest.java
package com.example.pauseapp.model;

public class UserRelationUpdateRequest {
    private String status;

    public UserRelationUpdateRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
