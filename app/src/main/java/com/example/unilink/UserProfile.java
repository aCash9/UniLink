package com.example.unilink;

public class UserProfile {
    String name;
    String email;
    String username;
    String clubCode;

    public UserProfile(String name, String email, String username, String clubCode) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.clubCode = clubCode;
    }

    public String getClubCode() {
        return clubCode;
    }

    public void setClubCode(String clubCode) {
        this.clubCode = clubCode;
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
