package com.uninaswap.model;

import java.time.LocalDate;

public class Offer {
    private Integer offerID;
    private int listingID;
    private int userID;
    private double amount;
    private String message;
    private typeOffer typeOffer;
    private InsertionStatus insertionStatus;
    private LocalDate offerDate;

    public Offer() {
    }

    public Offer(int listingID, int userID, double amount, String message,typeOffer typeOffer, InsertionStatus insertionStatus, LocalDate offerDate) {
        this.listingID = listingID;
        this.userID = userID;
        this.amount = amount;
        this.message = message;
        this.insertionStatus = insertionStatus;
        this.offerDate = offerDate;
        this.typeOffer = typeOffer;
    }

    public Integer  getOfferID() {
        return offerID;
    }

    public void setOfferID(Integer offerID) {
        this.offerID = offerID;
    }

    public int getListingID() {
        return listingID;
    }

    public void setListingID(int listingID) {
        this.listingID = listingID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InsertionStatus getListingStatus() {
        return insertionStatus;
    }

    public void setListingStatus(InsertionStatus insertionStatus) {
        this.insertionStatus = insertionStatus;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    public void setTypeOffer(typeOffer typeoffer) {
        this.typeOffer = typeoffer;
    }

    public typeOffer getTypeOffer() {
        return typeOffer;
    }


    @Override
    public String toString() {
        return "Offer{" +
                "offerID=" + offerID +
                ", listingID=" + listingID +
                ", userID=" + userID +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", listingStatus=" + insertionStatus +
                ", offerDate=" + offerDate +
                '}';
    }

}
