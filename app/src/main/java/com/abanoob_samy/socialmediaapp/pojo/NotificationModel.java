package com.abanoob_samy.socialmediaapp.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificationModel {

    private String id, uid, notification;

    @ServerTimestamp
    private Date timeStamp;

    public NotificationModel() {
    }

    public NotificationModel(String id, String uid, String notification, Date timeStamp) {
        this.id = id;
        this.uid = uid;
        this.notification = notification;
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
