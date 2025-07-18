package com.uninaswap.model;

import java.time.LocalDate;

public class Offer {
    private Integer offerID;
    private int insertionID;
    private int userID;
    private double amount;
    private String message;
    private typeOffer typeOffer;
    private InsertionStatus insertionStatus;
    private LocalDate offerDate;

    public Offer() {
    }

    public Offer(int insertionID, int userID, double amount, String message, typeOffer typeOffer, InsertionStatus insertionStatus, LocalDate offerDate) {
        this.insertionID = insertionID;
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

    public int getInsertionID() {
        return insertionID;
    }

    public void setInsertionID(int insertionID) {
        this.insertionID = insertionID;
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

    public InsertionStatus getInsertionStatus() {
        return insertionStatus;
    }

    public void setInsertionStatus(InsertionStatus insertionStatus) {
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
                ", insertionID=" + insertionID +
                ", userID=" + userID +
                ", amount=" + amount +
                ", message='" + message + '\'' +
                ", insertionStatus=" + insertionStatus +
                ", offerDate=" + offerDate +
                '}';
    }

}
