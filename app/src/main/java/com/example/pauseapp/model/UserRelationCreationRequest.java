// UserRelationCreationRequest.java
package com.example.pauseapp.model;

public class UserRelationCreationRequest {
    private Long senderId;
    private Long receiverId;

    public UserRelationCreationRequest(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}

