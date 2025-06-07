package com.uninaswap.model;

public class OfferedItem {
    private int offeredItemId;
    private int offerId;
    private int listingId;
    private String offeredItemDescription;
    private double amount;

    public OfferedItem() {}

    public OfferedItem(int offerId, int listingId, String offeredItemDescription, double amount) {
        this.offerId = offerId;
        this.listingId = listingId;
        this.offeredItemDescription = offeredItemDescription;
        this.amount = amount;
    }

    public int getOfferedItemId() {
        return offeredItemId;
    }

    public void setOfferedItemId(int offeredItemId) {
        this.offeredItemId = offeredItemId;
    }

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public String getOfferedItemDescription() {
        return offeredItemDescription;
    }

    public void setOfferedItemDescription(String offeredItemDescription) {
        this.offeredItemDescription = offeredItemDescription;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OfferedItem{" +
                "offeredItemId=" + offeredItemId +
                ", offerId=" + offerId +
                ", listingId=" + listingId +
                ", offeredItemDescription='" + offeredItemDescription + '\'' +
                ", amount=" + amount +
                '}';
    }
}
