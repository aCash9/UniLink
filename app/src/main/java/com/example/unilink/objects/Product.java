package com.example.unilink.objects;

public class Product {
    String productID;
    String amount;
    String contactInfo;
    String description;
    String productImage1;
    String productImage2;
    String productImage3;
    String timestamp;
    String userUID;

    public Product() {
    }
    public Product(String productID, String amount, String contactInfo, String description, String productImage1, String productImage2, String productImage3, String timestamp, String userUID) {
        this.productID = productID;
        this.amount = amount;
        this.contactInfo = contactInfo;
        this.description = description;
        this.productImage1 = productImage1;
        this.productImage2 = productImage2;
        this.productImage3 = productImage3;
        this.timestamp = timestamp;
        this.userUID = userUID;
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

    public String getProductImage1() {
        return productImage1;
    }

    public String getProductImage2() {
        return productImage2;
    }

    public String getProductImage3() {
        return productImage3;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getUserUID() {
        return userUID;
    }
}
