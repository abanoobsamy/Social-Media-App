package com.abanoob_samy.socialmediaapp.pojo;

public class UsersModel {

    private String profilePicture, username, email, uid, status;

    public UsersModel() {
    }

    public UsersModel(String profilePicture, String username, String email, String uid, String status) {
        this.profilePicture = profilePicture;
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.status = status;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
