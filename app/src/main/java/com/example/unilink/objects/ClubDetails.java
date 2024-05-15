package com.example.unilink.objects;

public class ClubDetails {
    String name;
    String clubImageURL;
    public ClubDetails() {
    }
    public ClubDetails(String name, String clubImageURL) {
        this.name = name;
        this.clubImageURL = clubImageURL;
    }
    public String getName() {
        return name;
    }
    public String getClubImageURL() {
        return clubImageURL;
    }
}
