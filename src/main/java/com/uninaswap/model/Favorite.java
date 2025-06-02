package com.uninaswap.model;

import java.sql.Timestamp;

public class Favorite {
    private int favoriteId;
    private int userId;
    private int listingId;
    private Timestamp createdAt;

    // Constructors
    public Favorite() {}

    public Favorite(int userId, int listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    // Getters and setters
    public int getFavoriteId() { return favoriteId; }
    public void setFavoriteId(int favoriteId) { this.favoriteId = favoriteId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getListingId() { return listingId; }
    public void setListingId(int listingId) { this.listingId = listingId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}