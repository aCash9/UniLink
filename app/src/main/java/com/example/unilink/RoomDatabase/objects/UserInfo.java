package com.example.unilink.RoomDatabase.objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info")
public class UserInfo {
    @ColumnInfo(name = "isAClub")
    private boolean isAClub;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "userImageUri")
    private String userImageUri;

    @PrimaryKey
    @NonNull
    private String userUID;

    @ColumnInfo(name = "username")
    private String username;


    public UserInfo(boolean isAClub, String name, String email, String userImageUri, @NonNull String userUID, String username) {
        this.isAClub = isAClub;
        this.name = name;
        this.email = email;
        this.userImageUri = userImageUri;
        this.userUID = userUID;
        this.username = username;
    }

    public boolean isAClub() {
        return isAClub;
    }

    public void setAClub(boolean AClub) {
        isAClub = AClub;
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

    public String getUserImageUri() {
        return userImageUri;
    }

    public void setUserImageUri(String userImageUri) {
        this.userImageUri = userImageUri;
    }

    @NonNull
    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(@NonNull String userUID) {
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
