package com.abanoob_samy.socialmediaapp.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class MessagesModel {

    private String id, lastMessage;

    private List<String> uid;

    @ServerTimestamp
    private Date timeStamp;

    public MessagesModel() {
    }

    public MessagesModel(List<String> uid, String id, String lastMessage, Date timeStamp) {
        this.uid = uid;
        this.id = id;
        this.lastMessage = lastMessage;
        this.timeStamp = timeStamp;
    }

    public List<String> getUid() {
        return uid;
    }

    public void setUid(List<String> uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
