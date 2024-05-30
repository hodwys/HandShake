package com.example.handshake;

public class SavedDonation {
    String donationName, donationInfo, donationLocation, donationType, donorName, donorPhone;
    String recipientID, UserID, donationID;
    float Rating;


    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getDonationLocation() {
        return donationLocation;
    }

    public void setDonationLocation(String donationLocation) {
        this.donationLocation = donationLocation;
    }

    public float getRating() {
        return Rating;
    }

    public void setRating(float rating) {
        Rating = rating;
    }

    public String getDonationName() {
        return donationName;
    }

    public String getDonationInfo() {
        return donationInfo;
    }


    public String getDonorName() {
        return donorName;
    }

    public String getDonorPhone() {
        return donorPhone;
    }

    public String getRecipientID() {
        return recipientID;
    }


    public String getDonationID() {
        return donationID;
    }

    public void setDonationName(String donationName) {
        this.donationName = donationName;
    }

    public void setDonationInfo(String donationInfo) {
        this.donationInfo = donationInfo;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public void setDonorPhone(String donorPhone) {
        this.donorPhone = donorPhone;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

//    public void setDonorID(String donorID) {
//        this.donorID = donorID;
//    }

    public void setDonationID(String donationID) {
        this.donationID = donationID;
    }
}