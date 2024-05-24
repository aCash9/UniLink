package com.example.unilink.objects;

public class UserProfile {
    String name;
    String email;
    String username;
    boolean isAClub;
    String userUID;
    String userImageURI;

    public UserProfile(String name, String email, String username, boolean isAClub, String userUID, String userImageURI) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.isAClub = isAClub;
        this.userUID = userUID;
        this.userImageURI = userImageURI;
    }

    public String getUserImageURI() {
        return userImageURI;
    }

    public void setUserImageURI(String userImageURI) {
        this.userImageURI = userImageURI;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public boolean isAClub() {
        return isAClub;
    }

    public void setAClub(boolean AClub) {
        isAClub = AClub;
    }

    public UserProfile() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
