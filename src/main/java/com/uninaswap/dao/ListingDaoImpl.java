package com.uninaswap.dao;

import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Category;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;
import com.uninaswap.model.typeListing;
import com.uninaswap.services.UserSession;
import com.uninaswap.utility.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListingDaoImpl implements ListingDao {

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
                    e.printStackTrace();
                    System.err.println("Errore nel processare la lista " + e.getMessage());
                }
            }
        }
        return listings;
    }
    @Override
    public List<Listing> findWithFilters(FilterCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT l.*, c.name as category_name FROM listings l ");
        sql.append("LEFT JOIN category c ON l.category_id = c.category_id ");
        sql.append("LEFT JOIN users u ON l.userId = u.userId ");
        sql.append("WHERE 1=1 ");

        List<Object> parameters = new ArrayList<>();

        // Filtro per stato (sempre AVAILABLE se non specificato)
        if (criteria.getStatus() != null) {
            sql.append("AND l.status = ? ");
            parameters.add(criteria.getStatus());
        } else {
            sql.append("AND l.status = 'AVAILABLE' ");
        }

        // Esclude l'utente corrente se specificato
        if (criteria.getExcludeUserId() != null) {
            sql.append("AND l.userid != ? ");
            parameters.add(criteria.getExcludeUserId());
        }

        // Filtro per testo (titolo e descrizione)
        if (criteria.hasTextSearch()) {
            sql.append("AND (l.title LIKE ? OR l.description LIKE ?) ");
            String searchPattern = "%" + criteria.getSearchText() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        // Filtro per categorie multiple
        if (criteria.hasCategoryFilter() && !criteria.getCategoryIds().contains(-1)) {
            sql.append("AND l.category_id IN (");
            for (int i = 0; i < criteria.getCategoryIds().size(); i++) {
                sql.append("?");
                if (i < criteria.getCategoryIds().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
            parameters.addAll(criteria.getCategoryIds());
        }

        // Filtro per prezzo
        if (criteria.hasPriceFilter()) {
            if (criteria.getMinPrice() != null) {
                sql.append("AND l.price >= ? ");
                parameters.add(criteria.getMinPrice());
            }
            if (criteria.getMaxPrice() != null) {
                sql.append("AND l.price <= ? ");
                parameters.add(criteria.getMaxPrice());
            }
        }

        if (criteria.hasFacultyFilter()) {
            sql.append("AND u.faculty IN (");
            for (int i = 0; i < criteria.getFacultyNames().size(); i++) {
                sql.append("?");
                if (i < criteria.getFacultyNames().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
            parameters.addAll(criteria.getFacultyNames());
        }

        if(criteria.hasTypeListingFilter()) {
            sql.append("AND l.type IN (");
            for (int i = 0; i < criteria.getTypes().size(); i++) {
                sql.append("?");
                if (i < criteria.getTypes().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
            //A quanto pare java non gli enum come paramentri per le query, quindi li devo convertire in stringhe
            for (typeListing type : criteria.getTypes()) {
                parameters.add(type.name());
            }
        }

        // Ordinamento
        sql.append("ORDER BY ");
        if ("price_asc".equals(criteria.getSortBy())) {
            sql.append("l.price ASC ");
        } else if ("price_desc".equals(criteria.getSortBy())) {
            sql.append("l.price DESC ");
        } else {
            sql.append("l.publishDate DESC "); // Default: più recenti
        }

        return executeFilterQuery(sql.toString(), parameters);
    }
    @Override
    public BigDecimal getMaxPrice() throws SQLException {
        String sql = "SELECT MAX(price) as max_price FROM listings WHERE status = 'AVAILABLE' AND price IS NOT NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal maxPrice = rs.getBigDecimal("max_price");
                return maxPrice != null ? maxPrice : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMinPrice() throws SQLException {
        String sql = "SELECT MIN(price) as min_price FROM listings WHERE status = 'AVAILABLE' AND price IS NOT NULL AND price > 0";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal minPrice = rs.getBigDecimal("min_price");
                return minPrice != null ? minPrice : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    private List<Listing> executeFilterQuery(String sql, List<Object> parameters) throws SQLException {
        List<Listing> listings = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Imposta i parametri
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Listing listing = new Listing();
                    mapResultSetToListing(rs, listing);
                    listings.add(listing);
                }
            }
        }

        return listings;
    }
    @Override
    public List<Listing> findAllOtherInsertions() throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.status = 'AVAILABLE' AND l.userid != ? " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());

            executeQueryAndCreateTheList(listings, stmt);
        }
        return listings;
    }
    @Override
    public List<Listing> findMyInsertions() throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.status = 'AVAILABLE' AND l.userid = ? " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(listings, stmt);
        }
        return listings;
    }

    @Override
    public List<Listing> findByCategory(int categoryId) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.category_id = ? AND l.status = 'AVAILABLE' AND l.userid != ? " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            stmt.setInt(2, UserSession.getInstance().getCurrentUser().getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Listing listing = new Listing();
                        mapResultSetToListing(rs, listing);
                        String categoryName = rs.getString("category_name");
                        listing.setCategory(categoryName != null ? categoryName : "Altro");

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
    public List<Listing> findByStatus(String status) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.status = ? AND l.userid != ? " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(listings, stmt);
        }
        return listings;
    }
    public List<Listing> findByFaculty(int facultyId) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.faculty_id = ? AND l.status = 'AVAILABLE' AND l.userid != ? " +
                "ORDER BY l.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            stmt.setInt(2, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(listings, stmt);
        }
        return listings;
    }
    public List<Listing> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE price BETWEEN ? AND ? AND l.userid = ? ORDER BY publishDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            stmt.setInt(3, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(listings, stmt);
        }
        return listings;
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
                        listing.setPublishDate(rs.getDate("publishDate").toLocalDate());
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
        stmt.setDate(7, new java.sql.Date(listing.getPublishDate().toEpochDay() * 24 * 60 * 60 * 1000)); // Convert LocalDate to java.sql.Date
        stmt.setInt(8, listing.getUserId());

        CategoryDaoImpl category = new CategoryDaoImpl();
        int categoryId = category.getCategoryIdByName(listing.getCategory());
        stmt.setInt(9, categoryId);
    }

    private void mapResultSetToListing(ResultSet rs, Listing listing) throws SQLException {
        listing.setListingId(rs.getInt("listingId"));
        listing.setTitle(rs.getString("title"));
        listing.setImageUrl(rs.getString("imageUrl"));
        listing.setDescription(rs.getString("description"));
        listing.setType(parseType(rs.getString("type")));
        listing.setPrice(rs.getBigDecimal("price"));
        listing.setStatus(parseStatus(rs.getString("status")));
        listing.setPublishDate(rs.getDate("publishDate").toLocalDate());
        listing.setUserId(rs.getInt("userId"));
        listing.setCategory(rs.getString("category_name"));
    }
    private void executeQueryAndCreateTheList(List<Listing> listings, PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    Listing listing = new Listing();
                    mapResultSetToListing(rs, listing);
                    listings.add(listing);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Errore nel processare la lista " + e.getMessage());
                }
            }
        }
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
        e.printStackTrace();
        System.err.println("Errore durante l’operazione '%s': %s%n" + operation +  e.getMessage());
    }
}
