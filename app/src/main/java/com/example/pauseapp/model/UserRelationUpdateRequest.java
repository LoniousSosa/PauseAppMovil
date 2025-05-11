// UserRelationUpdateRequest.java
package com.example.pauseapp.model;

public class UserRelationUpdateRequest {
    private boolean status;

    public UserRelationUpdateRequest(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
