package com.uninaswap.model;

public enum ListingStatus {

    AVAILABLE("AVAILABLE"),
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    SOLD("SOLD");

    private final String status;

    ListingStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
