package com.uninaswap.dao;

import java.util.List;
import java.util.ArrayList;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.utility.DatabaseUtil;

public class FavoriteDaoImpl implements FavoriteDao {

    @Override
    public void addFavorite(int userId, int listingId) throws SQLException {
        String sql = "INSERT INTO favorites (userId, listingId, createdAt) VALUES (?, ?, NOW())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.executeUpdate();
        }
    }

    public void removeFavorite(int userId, int listingId) throws SQLException {
        String sql = "DELETE FROM favorites WHERE userId = ? AND listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            stmt.executeUpdate();
        }
    }

    public List<Listing> getFavoritesByUserId(int userId) throws SQLException {
        String sql = "SELECT l.* FROM listings l " +
                "JOIN favorites f ON l.listingId = f.listingId " +
                "WHERE f.userId = ? " +
                "ORDER BY f.createdAt DESC";

        List<Listing> favorites = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Listing listing = new Listing();
                    listing.setListingId(rs.getInt("listingId"));
                    listing.setTitle(rs.getString("title"));
                    listing.setImageUrl(rs.getString("imageUrl"));
                    listing.setDescription(rs.getString("description"));
                    listing.setType(parseType(rs.getString("type")));
                    listing.setPrice(rs.getBigDecimal("price"));
                    listing.setStatus(parseStatus(rs.getString("status")));

                    // Handle publishDate correctly
                    Date sqlDate = rs.getDate("publishDate");
                    if (sqlDate != null) {
                        listing.setPublishDate(sqlDate.toLocalDate());
                    }

                    listing.setUserId(rs.getInt("userId"));

                    // You may need to handle category separately if it's not directly in this result set
                    try {
                        int categoryId = rs.getInt("category_id");
                        if (categoryId > 0) {
                            listing.setCategoryId(categoryId);
                        }
                    } catch (SQLException e) {
                        // Category might not be in the result set, that's fine
                    }

                    favorites.add(listing);
                }
            }
        }
        return favorites;
    }

    public boolean isFavorite(int userId, int listingId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM favorites WHERE userId = ? AND listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private typeListing parseType(String typeStr) {
        try {
            return typeListing.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            return typeListing.SALE; // Default value
        }
    }

    private ListingStatus parseStatus(String statusStr) {
        try {
            return ListingStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            return ListingStatus.AVAILABLE; // Default value
        }
    }
}