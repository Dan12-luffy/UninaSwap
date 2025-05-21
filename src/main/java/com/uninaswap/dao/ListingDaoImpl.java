package com.uninaswap.dao;

import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.utility.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ListingDaoImpl implements ListingDao {


    private static final String DEFAULT_CATEGORY = "Altro";

    @Override
    public void insert(Listing listing) {
        String sql = "INSERT INTO listings (title, imageUrl, description, type, price, status, publishDate, userId, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            populateListingStatement(stmt, listing);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logDatabaseError("Insert", e);
        }
    }

    public void delete(int listingId) {
        String sql = "DELETE FROM listings WHERE listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Delete", e);
        }
    }

    public void update(Listing listing) {
        String sql = "UPDATE listings SET title = ?, imageUrl = ?, description = ?, type = ?, price = ?, status = ?, publishDate = ?, userId = ?, category_id = ? WHERE listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            populateListingStatement(stmt, listing);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Update", e);
        }

    }

    @Override
    public List<Listing> findAll() throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Listing listing = new Listing();
                    mapResultSetToListing(rs, listing);
                    listings.add(listing);
                } catch (Exception e) {
                    System.err.println("Errore nel processare la lista " + e.getMessage());
                }
            }
        }
        return listings;
    }

    @Override
    public List<Listing> findByCategory(int categoryId) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.category_id = ? AND l.status = 'AVAILABLE' " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Listing listing = new Listing();
                        mapResultSetToListing(rs, listing);

                        // Override the category from mapResultSetToListing with the joined category name
                        String categoryName = rs.getString("category_name");
                        listing.setCategory(categoryName != null ? categoryName : DEFAULT_CATEGORY);

                        listings.add(listing);
                    } catch (Exception e) {
                        System.err.println("Errore nel processare la lista: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero degli annunci per categoria " + categoryId + ": " + e.getMessage());
            throw e;
        }
        return listings;
    }

    //TODO implementare la ricerca per prezzo
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

    //TODO implementare la ricerca per testo
    @Override
    public List<Listing> findByText(String text) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE (l.title LIKE ? OR l.description LIKE ?) " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchText = "%" + text + "%";
            stmt.setString(1, searchText);
            stmt.setString(2, searchText);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Listing listing = new Listing();
                        listing.setListingId(rs.getInt("listingId"));
                        listing.setTitle(rs.getString("title"));
                        listing.setImageUrl(rs.getString("imageUrl"));
                        listing.setDescription(rs.getString("description"));

                        String typeStr = rs.getString("type");
                        try {
                            listing.setType(typeListing.valueOf(typeStr));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Tipo sconosciuto " + typeStr + " - impostato a SALE di default");
                            listing.setType(typeListing.SALE);
                        }

                        String statusStr = rs.getString("status");
                        try {
                            listing.setStatus(ListingStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Stato sconosciuto: " + statusStr + " - impostato a AVAILABLE di default");
                            listing.setStatus(ListingStatus.AVAILABLE);
                        }

                        listing.setPrice(rs.getBigDecimal("price"));
                        listing.setPublishDate(rs.getDate("publishDate"));
                        listing.setUserId(rs.getInt("userId"));
                        String categoryName = rs.getString("category_name");
                        listing.setCategory(categoryName != null ? categoryName : "Altro");

                        listings.add(listing);
                    } catch (Exception e) {
                        System.err.println("Errore nel processare la lista " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la ricerca per testo: " + e.getMessage());
            throw e;
        }
        return listings;
    }


    private void populateListingStatement(PreparedStatement stmt, Listing listing) throws SQLException {
        stmt.setString(1, listing.getTitle());
        stmt.setString(2, listing.getImageUrl());
        stmt.setString(3, listing.getDescription());
        stmt.setString(4, listing.getType().name());
        stmt.setBigDecimal(5, listing.getPrice());
        stmt.setString(6, listing.getStatus().name());
        stmt.setDate(7, new java.sql.Date(listing.getPublishDate().getTime()));
        stmt.setInt(8, listing.getUserId());
        stmt.setString(9, listing.getCategory());
    }

    private void mapResultSetToListing(ResultSet rs, Listing listing) throws SQLException {
        listing.setListingId(rs.getInt("listingId"));
        listing.setTitle(rs.getString("title"));
        listing.setImageUrl(rs.getString("imageUrl"));
        listing.setDescription(rs.getString("description"));
        listing.setType(parseType(rs.getString("type")));
        listing.setPrice(rs.getBigDecimal("price"));
        listing.setStatus(parseStatus(rs.getString("status")));
        listing.setPublishDate(rs.getDate("publishDate"));
        listing.setUserId(rs.getInt("userId"));
        listing.setCategory(rs.getString("name"));
    }
    private typeListing parseType(String typeStr) {
        try {
            return typeListing.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Tipo sconosciuto " + typeStr + " - default a SALE");
            return typeListing.SALE;
        }
    }
    private ListingStatus parseStatus(String statusStr) {
        try {
            return ListingStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Stato sconosciuto " + statusStr + " - default a AVAILABLE");
            return ListingStatus.AVAILABLE;
        }
    }

    //TODO spostare questo in validationservice
    private  void logDatabaseError(String operation, Exception e){
        System.err.println("Errore durante l’operazione '%s': %s%n" + operation +  e.getMessage());
    }
}
