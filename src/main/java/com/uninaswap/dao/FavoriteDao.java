package com.uninaswap.dao;

import java.util.List;
import java.sql.SQLException;
import com.uninaswap.model.Listing;

public interface FavoriteDao {
    void addFavorite(int userId, int listingId);
    void removeFavorite(int userId, int listingId);
    boolean isFavorite(int userId, int listingId);
    List<Listing> getFavoritesByUserId(int userId);
    int getFavoriteCount(int listingId);
}
