package com.uninaswap.model;

import java.time.LocalDate;

public class Offer {
    private int offerID;
    private int listingID;
    private int userID;
    private double amount;
    private String message;
    private ListingStatus listingStatus;
    private LocalDate offerDate;

    public Offer() {
    }

    public Offer(int offerID, int listingID, int userID, double amount, String message, ListingStatus listingStatus, LocalDate offerDate) {
        this.offerID = offerID;
        this.listingID = listingID;
        this.userID = userID;
        this.amount = amount;
        this.message = message;
        this.listingStatus = listingStatus;
        this.offerDate = offerDate;
    }

    public int getOfferID() {
        return offerID;
    }

    public void setOfferID(int offerID) {
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

    public ListingStatus getListingStatus() {
        return listingStatus;
    }

    public void setListingStatus(ListingStatus listingStatus) {
        this.listingStatus = listingStatus;
    }

    public LocalDate getOfferDate() {
        return offerDate;
    }

    public void setOfferDate(LocalDate offerDate) {
        this.offerDate = offerDate;
    }

    @Override
    public String toString() {
        return "Offer{" +
                "offerID=" + offerID +
                ", listingID=" + listingID +
                ", userID=" + userID +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", listingStatus=" + listingStatus +
                ", offerDate=" + offerDate +
                '}';
    }
}
