package com.example.unilink.objects;

import com.google.firebase.Timestamp;

public class UserPosts {
    String username;
    String postID;
    int like;
    String userImageURL;
    String postImageURL;
    String caption;
    String timestamp;
    String userUID;

    public UserPosts() {}

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public UserPosts(String username, String postID, int like, String userImageURL, String postImageURL, String caption, String timestamp, String userUID) {
        this.username = username;
        this.postID = postID;
        this.like = like;
        this.userImageURL = userImageURL;
        this.postImageURL = postImageURL;
        this.caption = caption;
        this.timestamp = timestamp;
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public String getPostImageURL() {
        return postImageURL;
    }

    public void setPostImageURL(String postImageURL) {
        this.postImageURL = postImageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
