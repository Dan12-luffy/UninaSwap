package com.uninaswap.model;

import java.time.LocalDateTime;

public class Transaction {

    private int transactionId;
    private int listingId;
    private int sellerId;
    private int buyerId;
    private double amount;
    private String transactioType; // "PURCHASE", "SALE", etc.
    private String status; // "PENDING", "COMPLETED", "CANCELED"
    private LocalDateTime transactionDate;
    private String description;

    public Transaction(){}

    public Transaction(int listingId,int sellerId,int buyerId,double amount,String transactioType,String status,String description){
        this.listingId = listingId;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.amount = amount;
        this.transactioType = transactioType;
        this.status = status;
        this.description = description;
        this.transactionDate = LocalDateTime.now();
    }

    public int getTransactionId() { return transactionId; }
    public void setTransactionId(int transactionId) { this.transactionId = transactionId; }

    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public int getBuyerId() { return buyerId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getTransactionType() { return transactioType; }
    public void setTransactionType(String transactionType) { this.transactioType = transactionType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", listingId=" + listingId +
                ", sellerId=" + sellerId +
                ", buyerId=" + buyerId +
                ", amount=" + amount +
                ", transactionType='" + transactioType + '\'' +
                ", status='" + status + '\'' +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                '}';
    }







}
