package com.abanoob_samy.socialmediaapp.pojo;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class HomeModel {

    private String id, username, profilePicture, postImage, uid, description;

    @ServerTimestamp
    private Date timeStamp;

    private List<String> likes;

    private List<CommentsModel> commentsModelList;

    private boolean isLiked = false;

    public HomeModel() {
    }

    public HomeModel(String id, String username, Date timeStamp, String profilePicture, String postImage,
                     String uid, String description,
                     List<String> likes,
                     List<CommentsModel> commentsModelList, boolean isLiked) {
        this.id = id;
        this.username = username;
        this.timeStamp = timeStamp;
        this.profilePicture = profilePicture;
        this.postImage = postImage;
        this.uid = uid;
        this.description = description;
        this.likes = likes;
        this.isLiked = isLiked;
        this.commentsModelList = commentsModelList;
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

    public void setUsername(String userName) {
        this.username = userName;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public List<CommentsModel> getCommentsModelList() {
        return commentsModelList;
    }

    public void setCommentsModelList(List<CommentsModel> commentsModelList) {
        this.commentsModelList = commentsModelList;
    }
}
