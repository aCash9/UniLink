package com.example.unilink.RoomDatabase.objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "clubs")
public class ClubsInfo {
    String bannerURL;
    @PrimaryKey
    @NonNull
    String clubUID;

    public ClubsInfo() {
    }

    public ClubsInfo(String bannerURL, String clubUID) {
        this.bannerURL = bannerURL;
        this.clubUID = clubUID;
    }

    public String getBannerURL() {
        return bannerURL;
    }

    public void setBannerURL(String bannerURL) {
        this.bannerURL = bannerURL;
    }

    public String getClubUID() {
        return clubUID;
    }

    public void setClubUID(String clubUID) {
        this.clubUID = clubUID;
    }
}
