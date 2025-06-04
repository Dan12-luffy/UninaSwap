package com.uninaswap.dao;

import com.uninaswap.model.Listing;
import com.uninaswap.utility.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteDaoImpl implements FavoriteDao {

    @Override
    public void addFavorite(int userId, int listingId) {
        String sql = "INSERT INTO favorites (userid, listingid) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Add Favorite", e);
        }
    }

    @Override
    public void removeFavorite(int userId, int listingId) {
        String sql = "DELETE FROM favorites WHERE userid = ? AND listingid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Remove Favorite", e);
        }
    }

    @Override
    public boolean isFavorite(int userId, int listingId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE userid = ? AND listingid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logDatabaseError("Check Favorite", e);
        }
        return false;
    }

    @Override
    public List<Listing> getFavoritesByUserId(int userId) {
        List<Listing> favorites = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM favorites f " +
                "JOIN listings l ON f.listingid = l.listingId " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE f.userid = ? " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);

            ListingDaoImpl listingDao = new ListingDaoImpl();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        // Use the createListingFromResultSet from ListingDaoImpl
                        // This ensures we create the correct subclass based on type
                        Listing listing = listingDao.createListingFromResultSet(rs);
                        favorites.add(listing);
                    } catch (Exception e) {
                        System.err.println("Errore nel processare l'annuncio preferito: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            logDatabaseError("Get User Favorites", e);
        }
        return favorites;
    }

    @Override
    public int getFavoriteCount(int listingId) {
        String sql = "SELECT COUNT(*) FROM favorites WHERE listingid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logDatabaseError("Get Favorite Count", e);
        }
        return 0;
    }

    private void logDatabaseError(String operation, Exception e) {
        e.printStackTrace();
        System.err.println("Errore durante l'operazione '" + operation + "': " + e.getMessage());
    }
}