package com.uninaswap.dao;

import java.util.List;
import java.sql.SQLException;
import com.uninaswap.model.Listing;

public interface FavoriteDao {

    void addFavorite(int userId, int listingId) throws SQLException;
    void removeFavorite(int userId, int listingId) throws SQLException;
    List<Listing> getFavoritesByUserId(int userId) throws SQLException;
    boolean isFavorite(int userId, int listingId) throws SQLException;


}
