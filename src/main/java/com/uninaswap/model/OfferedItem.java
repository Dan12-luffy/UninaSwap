package com.uninaswap.model;

public class OfferedItem {
    private int offeredItemId;
    private int offerId;
    private int insertionID;

    public OfferedItem() {}

    public OfferedItem(int offerId, int insertionID) {
        this.offerId = offerId;
        this.insertionID = insertionID;
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
        return insertionID;
    }

    public void setInsertionID(int insertionID) {
        this.insertionID = insertionID;
    }

    @Override
    public String toString() {
        return "OfferedItem{" +
                "offeredItemId=" + offeredItemId +
                ", offerId=" + offerId +
                ", insertionID=" + insertionID +
                '}';
    }
}
