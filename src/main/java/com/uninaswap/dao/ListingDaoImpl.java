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
    public void insert(Listing listing) {
        String sql = "INSERT INTO listings (title, imageUrl, description, type, price, status, publishDate, userId, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            stmt.setString(9, listing.getCategory());
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int listingId) {
        String sql = "DELETE FROM listings WHERE listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void update(Listing listing){
        String sql = "UPDATE listings SET title = ?, imageUrl = ?, description = ?, type = ?, price = ?, status = ?, publishDate = ?, userId = ?, category_id = ? WHERE listingId = ?";
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
            stmt.setString(9, listing.getCategory());
            stmt.setInt(10, listing.getListingId());
            stmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public List<Listing> findAll() throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.status = 'ACTIVE' ORDER BY l.publishDate DESC";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

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
                listing.setCategoryId(rs.getInt("category_id"));

                // Salviamo il nome della categoria
                String categoryName = rs.getString("category_name");
                if (categoryName == null) {
                    categoryName = "Altro"; // Valore di default
                }
                listing.setCategory(categoryName);

                listings.add(listing);
            }
        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
            throw e;
        } finally {
            // Chiusura delle risorse
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignora */ }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { /* ignora */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignora */ }
        }

        return listings;
    }



    @Override
    public List<Listing> findByCategory(int categoryId){
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT * FROM listings WHERE category_id = ? AND status = 'Available' ORDER BY publishDate DESC";
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
                    listing.setCategory(rs.getString("name"));
                    listings.add(listing);
                }
                return listings;
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Listing> findByType(String type){
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
                    listing.setCategory(rs.getString("name"));
                    listings.add(listing);
                }
            }
            return listings;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Listing> findByPriceRange(double minPrice, double maxPrice) {
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
                    listing.setCategory(rs.getString("name"));
                }
            }
            return listings;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
