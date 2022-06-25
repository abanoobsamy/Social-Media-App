package com.abanoob_samy.socialmediaapp.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class StoriesModel {

    private String uid, id, username, url, type, profilePicture;

    @ServerTimestamp
    private Date timeStamp;

    public StoriesModel() {
    }

    public StoriesModel(String uid, String id, String url, String type, String username, String profilePicture) {
        this.uid = uid;
        this.id = id;
        this.username = username;
        this.url = url;
        this.type = type;
        this.profilePicture = profilePicture;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String videoUrl) {
        this.url = videoUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
