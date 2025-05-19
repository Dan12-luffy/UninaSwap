package com.uninaswap.dao;

import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.utility.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ListingDaoImpl implements ListingDao {

    @Override
    public void insert(Listing listing) throws SQLException {
        String sql = "INSERT INTO listings (title, imageUrl, description, type, price, status, publishDate, userId, categoryId) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, listing.getTitle());
            stmt.setString(2, listing.getImageUrl());
            stmt.setString(3, listing.getDescription());
            stmt.setString(4, listing.getType().name());
            stmt.setBigDecimal(5, listing.getPrice());
            stmt.setString(6, listing.getStatus().name());
            stmt.setDate(7, new java.sql.Date(listing.getPublishDate().getTime()));
            stmt.setInt(8, listing.getUserId());
            stmt.setInt(9, listing.getCategoryId());
            stmt.executeUpdate();
        }
    }

    public void delete(int listingId) throws SQLException {
        String sql = "DELETE FROM listings WHERE listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            stmt.executeUpdate();
        }
    }

    public void update(Listing listing) throws SQLException {
        String sql = "UPDATE listings SET title = ?, imageUrl = ?, description = ?, type = ?, price = ?, status = ?, publishDate = ?, userId = ?, categoryId = ? WHERE listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, listing.getTitle());
            stmt.setString(2, listing.getImageUrl());
            stmt.setString(3, listing.getDescription());
            stmt.setString(4, listing.getType().name());
            stmt.setBigDecimal(5, listing.getPrice());
            stmt.setString(6, listing.getStatus().name());
            stmt.setDate(7, new java.sql.Date(listing.getPublishDate().getTime()));
            stmt.setInt(8, listing.getUserId());
            stmt.setInt(9, listing.getCategoryId());
            stmt.setInt(10, listing.getListingId());
            stmt.executeUpdate();
        }

    }

    @Override
    public List<Listing> findAll() throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE status = 'Available' ORDER BY publishDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Listing listing = new Listing();
                listing.setListingId(rs.getInt("listingId"));
                listing.setTitle(rs.getString("title"));
                listing.setImageUrl(rs.getString("imageUrl"));
                listing.setDescription(rs.getString("description"));
                listing.setType(typeListing.valueOf(rs.getString("type")));
                listing.setPrice(rs.getBigDecimal("price"));
                listing.setStatus(ListingStatus.valueOf(rs.getString("status")));
                listing.setPublishDate(rs.getDate("publishDate"));
                listing.setUserId(rs.getInt("userId"));
                listing.setCategoryId(rs.getInt("categoryId"));
                listings.add(listing);
            }
        }
        return listings;
    }


    @Override
    public List<Listing> findByCategory(int categoryId) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE categoryId = ? AND status = 'Available' ORDER BY publishDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Listing listing = new Listing();
                    listing.setListingId(rs.getInt("listingId"));
                    listing.setTitle(rs.getString("title"));
                    listing.setImageUrl(rs.getString("imageUrl"));
                    listing.setDescription(rs.getString("description"));
                    listing.setType(typeListing.valueOf(rs.getString("type")));
                    listing.setPrice(rs.getBigDecimal("price"));
                    listing.setStatus(ListingStatus.valueOf(rs.getString("status")));
                    listing.setPublishDate(rs.getDate("publishDate"));
                    listing.setUserId(rs.getInt("userId"));
                    listing.setCategoryId(rs.getInt("categoryId"));
                    listings.add(listing);
                }
            }
        }
        return listings;
    }

    @Override
    public List<Listing> findByType(String type) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE type = ? AND status = 'Available' ORDER BY publishDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Listing listing = new Listing();
                    listing.setListingId(rs.getInt("listingId"));
                    listing.setTitle(rs.getString("title"));
                    listing.setImageUrl(rs.getString("imageUrl"));
                    listing.setDescription(rs.getString("description"));
                    listing.setType(typeListing.valueOf(rs.getString("type")));
                    listing.setPrice(rs.getBigDecimal("price"));
                    listing.setStatus(ListingStatus.valueOf(rs.getString("status")));
                    listing.setPublishDate(rs.getDate("publishDate"));
                    listing.setUserId(rs.getInt("userId"));
                    listing.setCategoryId(rs.getInt("categoryId"));
                    listings.add(listing);
                }
            }
        }
        return listings;
    }

    public List<Listing> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE price BETWEEN ? AND ? AND status = 'Available' ORDER BY publishDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Listing listing = new Listing();
                    listing.setListingId(rs.getInt("listingId"));
                    listing.setTitle(rs.getString("title"));
                    listing.setImageUrl(rs.getString("imageUrl"));
                    listing.setDescription(rs.getString("description"));
                    listing.setType(typeListing.valueOf(rs.getString("type")));
                    listing.setPrice(rs.getBigDecimal("price"));
                    listing.setStatus(ListingStatus.valueOf(rs.getString("status")));
                    listing.setPublishDate(rs.getDate("publishDate"));
                    listing.setUserId(rs.getInt("userId"));
                    listing.setCategoryId(rs.getInt("categoryId"));

                }
            }
            return listings;
        }
    }
}
