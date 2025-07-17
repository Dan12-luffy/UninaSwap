package com.uninaswap.model;

public class OfferedItem {
    private int offeredItemId;
    private int offerId;
    private int listingId;

    public OfferedItem() {}

    public OfferedItem(int offerId, int listingId) {
        this.offerId = offerId;
        this.listingId = listingId;
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

    public int getInsertionId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    @Override
    public String toString() {
        return "OfferedItem{" +
                "offeredItemId=" + offeredItemId +
                ", offerId=" + offerId +
                ", listingId=" + listingId +
                '}';
    }
}
