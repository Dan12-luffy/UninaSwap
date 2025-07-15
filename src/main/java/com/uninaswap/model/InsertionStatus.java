package com.uninaswap.model;

public enum InsertionStatus {

    AVAILABLE("AVAILABLE"),
    PENDING("PENDING"),
    ACCEPTED("ACCEPTED"),
    REJECTED("REJECTED"),
    SOLD("SOLD"),
    DISMISSED("DISMISSED");

    private final String status;

    InsertionStatus(String status) {
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
