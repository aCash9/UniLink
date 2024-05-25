package com.example.unilink.objects;

import java.util.ArrayList;

public class Product {
    String productID;
    String amount;
    String contactInfo;
    String description;
    ArrayList<String> images;
    String timestamp;
    String userUID;

    public Product() {
    }

    public Product(String productID, String amount, String contactInfo, String description, ArrayList<String> images, String timestamp, String userUID) {
        this.productID = productID;
        this.amount = amount;
        this.contactInfo = contactInfo;
        this.description = description;
        this.images = images;
        this.timestamp = timestamp;
        this.userUID = userUID;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public String getProductID() {
        return productID;
    }

    public String getAmount() {
        return amount;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public String getDescription() {
        return description;
    }


    public String getTimestamp() {
        return timestamp;
    }

    public String getUserUID() {
        return userUID;
    }
}
