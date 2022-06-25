package com.abanoob_samy.socialmediaapp.pojo;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostImageModel {

    private String id, postImage, description, uid;

    @ServerTimestamp
    private Date timeStamp;

    public PostImageModel() {
    }

    public PostImageModel(String id, String postImage, String description, Date timeStamp, String uid) {
        this.id = id;
        this.postImage = postImage;
        this.description = description;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String imageUrl) {
        this.postImage = imageUrl;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
