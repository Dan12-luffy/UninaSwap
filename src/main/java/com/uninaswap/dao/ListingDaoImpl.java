package com.uninaswap.dao;

import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.*;
import com.uninaswap.services.UserSession;
import com.uninaswap.utility.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListingDaoImpl implements ListingDao {

    @Override
    public void insert(Listing listing) throws SQLException {
        String sql = "INSERT INTO listings (title, imageUrl, description, type, price, status, publishDate, userId, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            System.out.println("Attendint to insert listing "+ listing.getTitle());
            populateListingStatement(stmt, listing);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            if (rowsAffected > 0) {
                System.out.println("Listing inserted successfully.");
            } else {
                System.out.println("No rows inserted. Check your data.");
            }

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
                    Listing listing = createListingFromResultSet(rs);
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
    public List<Listing> findByFilters(FilterCriteria criteria) throws SQLException {
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
        //Filtro per testo
        if (criteria.hasTextSearch()) {
            sql.append("AND (LOWER(l.title) LIKE LOWER(?) OR LOWER(l.description) LIKE LOWER(?)) ");
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
        //Filtro per facoltà
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
        // Filtro per tipo
        if(criteria.hasTypeListingFilter()) {
            sql.append("AND l.type IN (");
            for (int i = 0; i < criteria.getTypes().size(); i++) {
                sql.append("?");
                if (i < criteria.getTypes().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
            //A quanto pare java non prende gli enum come paramentri per le query, quindi li devo convertire in stringhe
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

    @Override
    public void updateListingStatus(int listingId, ListingStatus status) throws SQLException {
        String sql = "UPDATE listings SET status = ? WHERE listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, listingId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Stato dell'inserzione aggiornato con successo.");
            } else {
                System.out.println("Nessuna riga aggiornata. Controlla l'ID dell'inserzione.");
            }
        } catch (SQLException e) {
            logDatabaseError("Update Status", e);
        }
    }
    @Override
    public List<Listing> findInsertionsExcludingCurrentUser() throws SQLException {
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
    public List<Listing> findCurrentUserAvailableInsertions() throws SQLException {
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
    public List<Listing> findCurrentUserInsertions() throws SQLException {
        List<Listing> listings = new ArrayList<>();
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.userid = ? " +
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
                        Listing listing = createListingFromResultSet(rs);
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
    @Override
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

    @Override
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

    @Override
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
    @Override
    public Listing findListingById(int listingId) throws SQLException {
        String sql = "SELECT l.*, c.name as category_name FROM listings l " +
                "LEFT JOIN category c ON l.category_id = c.category_id " +
                "WHERE l.listingId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, listingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createListingFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dell'annuncio con ID " + listingId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    private void populateListingStatement(PreparedStatement stmt, Listing listing) throws SQLException {
        stmt.setString(1, listing.getTitle());
        stmt.setString(2, listing.getImageUrl());
        stmt.setString(3, listing.getDescription());
        stmt.setString(4, listing.getType().name());
        switch (listing) {
            case SaleListing saleListing -> stmt.setBigDecimal(5, saleListing.getPrice());
            case ExchangeListing exchangeListing -> stmt.setBigDecimal(5, exchangeListing.getPrice());
            case GiftListing giftListing -> stmt.setBigDecimal(5, BigDecimal.ZERO);
            default -> {
                stmt.setBigDecimal(5, BigDecimal.ZERO);
                throw new SQLException("Tipo di inserzione sconosciuto");
            }
        }

        stmt.setString(6, listing.getStatus().name());
        stmt.setDate(7, new java.sql.Date(listing.getPublishDate().toEpochDay() * 24 * 60 * 60 * 1000)); // Convert LocalDate to java.sql.Date
        stmt.setInt(8, listing.getUserId());

        CategoryDaoImpl category = new CategoryDaoImpl();
        int categoryId = category.getCategoryIdByName(listing.getCategory());
        stmt.setInt(9, categoryId);
        stmt.setInt(10, listing.getListingId());
    }

    public Listing createListingFromResultSet(ResultSet rs) throws SQLException {
        int listingId = rs.getInt("listingId");
        String title = rs.getString("title");
        String imageUrl = rs.getString("imageUrl");
        String description = rs.getString("description");
        typeListing type = parseType(rs.getString("type"));
        ListingStatus status = parseStatus(rs.getString("status"));
        java.time.LocalDate publishDate = rs.getDate("publishDate").toLocalDate();
        int userId = rs.getInt("userId");
        String category = rs.getString("category_name");
        BigDecimal price = rs.getBigDecimal("price");
        Listing listing = ListingFactory.createListing(title, imageUrl, description, type, price, status, publishDate, userId, category);

        listing.setListingId(listingId);

        return listing;
    }
    private void executeQueryAndCreateTheList(List<Listing> listings, PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    Listing listing = createListingFromResultSet(rs);
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
                    Listing listing = createListingFromResultSet(rs);
                    listings.add(listing);
                }
            }
        }
        return listings;
    }

    //TODO spostare questo in validationservice
    private  void logDatabaseError(String operation, Exception e){
        e.printStackTrace();
        System.err.println("Errore durante l’operazione '%s': %s%n" + operation +  e.getMessage());
    }
}
