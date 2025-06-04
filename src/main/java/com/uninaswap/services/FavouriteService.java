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
        int userId = UserSession.getInstance().getCurrentUserId();
        favoriteDao.addFavorite(userId, listingId);
    }
    public void removeFromFavorites(int listingId) {
        int userId = UserSession.getInstance().getCurrentUserId();
        favoriteDao.removeFavorite(userId, listingId);
    }
    public boolean isFavorite(int listingId){
        int userId = UserSession.getInstance().getCurrentUserId();
        return favoriteDao.isFavorite(userId, listingId);

    }
    // Fix in FavoriteService.java
    public List<Listing> getUserFavorites() {
        int userId = UserSession.getInstance().getCurrentUserId();
        return favoriteDao.getFavoritesByUserId(userId);
    }

    public void toggleFavorite(int listingId) {
        int userId = UserSession.getInstance().getCurrentUserId();
        if (favoriteDao.isFavorite(userId, listingId)) {
            favoriteDao.removeFavorite(userId, listingId);
        } else {
            favoriteDao.addFavorite(userId, listingId);
        }
    }
}
