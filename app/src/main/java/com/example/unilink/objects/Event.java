package com.example.unilink.objects;

public class Event {
    private String eid;
    private String title;
    private String description;
    private String link;
    private String clubID;
    private String timestamp;

    public Event() {
    }

    public Event(String eid, String title, String description, String link, String clubID, String timestamp) {
        this.eid = eid;
        this.title = title;
        this.description = description;
        this.link = link;
        this.clubID = clubID;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getClubID() {
        return clubID;
    }

    public void setClubID(String clubID) {
        this.clubID = clubID;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
