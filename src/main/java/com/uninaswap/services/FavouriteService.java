package com.uninaswap.services;

import com.uninaswap.dao.FavoriteDao;
import com.uninaswap.dao.FavoriteDaoImpl;
import com.uninaswap.model.Listing;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavouriteService {

    private static final FavouriteService instance = new FavouriteService();
    private final FavoriteDao favoriteDao;


    private FavouriteService() {
        this.favoriteDao = new FavoriteDaoImpl();
    }

    public static FavouriteService getInstance() {
        return instance;
    }
    public void addToFavorites(int listingId){
        try{
            int userId = UserSession.getInstance().getCurrentUserId();
            favoriteDao.addFavorite(userId, listingId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void removeFromFavorites(int listingId) {
        try {
            int userId = UserSession.getInstance().getCurrentUserId();
            favoriteDao.removeFavorite(userId, listingId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean isFavorite(int listingId){
        try{
            int userId = UserSession.getInstance().getCurrentUserId();
            return favoriteDao.isFavorite(userId, listingId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }
    // Fix in FavoriteService.java
    public List<Listing> getUserFavorites() {
        try {
            int userId = UserSession.getInstance().getCurrentUserId();
            return favoriteDao.getFavoritesByUserId(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(); // Return an empty list in case of error
        }
    }

    public void toggleFavorite(int listingId) {
        try {
            int userId = UserSession.getInstance().getCurrentUserId();
            if (favoriteDao.isFavorite(userId, listingId)) {
                favoriteDao.removeFavorite(userId, listingId);
            } else {
                favoriteDao.addFavorite(userId, listingId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
