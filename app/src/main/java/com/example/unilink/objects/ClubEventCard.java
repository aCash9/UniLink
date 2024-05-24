package com.example.unilink.objects;

public class ClubEventCard {
    String bannerURL;
    String clubUID;

    public ClubEventCard() {
    }

    public ClubEventCard(String bannerURL, String clubUID) {
        this.bannerURL = bannerURL;
        this.clubUID = clubUID;
    }

    public String getClubUID() {
        return clubUID;
    }

    public void setClubUID(String clubUID) {
        this.clubUID = clubUID;
    }

    public String getBannerURL() {
        return bannerURL;
    }

    public void setBannerURL(String bannerURL) {
        this.bannerURL = bannerURL;
    }
}
