package com.abanoob_samy.socialmediaapp.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class ChatsModel {

    private String id, message, senderId;

    @ServerTimestamp
    private Date timeStamp;

    public ChatsModel() {
    }

    public ChatsModel(String id, String message, String senderId, Date timeStamp) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
