package com.uninaswap.dao;

import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.*;
import com.uninaswap.services.UserSession;
import com.uninaswap.utility.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsertionDaoImpl implements InsertionDao {

    @Override
    public void insert(Insertion insertion) throws SQLException {
        String sql = "INSERT INTO insertion (title, imageUrl, description, delivery_method, type, price, status, publishDate, userId, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            populateInsertionStatement(stmt, insertion);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Insert", e);
            throw e;
        }
    }

    public void delete(int insertionID) {
        String sql = "DELETE FROM insertion WHERE insertionid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, insertionID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Delete", e);
        }
    }

    public void update(Insertion insertion) {
        String sql = "UPDATE insertion SET title = ?, imageUrl = ?, description = ?, delivery_method = ?, type = ?, price = ?, status = ?, publishDate = ?, userId = ?, category_id = ? WHERE insertionid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            populateInsertionStatement(stmt, insertion);
            stmt.setInt(11, insertion.getInsertionID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logDatabaseError("Update", e);
        }
    }

    @Override
    public List<Insertion> findAll() throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    Insertion insertion = createInsertionFromResultSet(rs);
                    insertions.add(insertion);
                } catch (SQLException e) {
                    logDatabaseError("FindAll", e);
                    System.err.println("Errore nel processare la lista " + e.getMessage());
                }
            }
        }
        return insertions;
    }

    @Override
    public List<Insertion> findByFilters(FilterCriteria criteria) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT i.*, c.name as category_name FROM insertion i ");
        sql.append("LEFT JOIN category c ON i.category_id = c.category_id ");
        sql.append("LEFT JOIN users u ON i.userId = u.userId ");
        sql.append("WHERE 1=1 ");

        List<Object> parameters = new ArrayList<>();

        if (criteria.getStatus() != null) {
            sql.append("AND i.status = ? ");
            parameters.add(criteria.getStatus());
        } else {
            sql.append("AND i.status = 'AVAILABLE' ");
        }

        if (criteria.getExcludeUserId() != null) {
            sql.append("AND i.userid != ? ");
            parameters.add(criteria.getExcludeUserId());
        }

        if (criteria.hasTextSearch()) {
            sql.append("AND (LOWER(i.title) LIKE LOWER(?) OR LOWER(i.description) LIKE LOWER(?)) ");
            String searchPattern = "%" + criteria.getSearchText() + "%";
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        if (criteria.hasCategoryFilter() && !criteria.getCategoryIds().contains(-1)) {
            sql.append("AND i.category_id IN (");
            for (int i = 0; i < criteria.getCategoryIds().size(); ++i) {
                sql.append("?");
                if (i < criteria.getCategoryIds().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
            parameters.addAll(criteria.getCategoryIds());
        }

        if (criteria.hasPriceFilter()) {
            if (criteria.getMinPrice() != null) {
                sql.append("AND i.price >= ? ");
                parameters.add(criteria.getMinPrice());
            }
            if (criteria.getMaxPrice() != null) {
                sql.append("AND i.price <= ? ");
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
            sql.append("AND i.type IN (");
            for (int i = 0; i < criteria.getTypes().size(); ++i) {
                sql.append("?");
                if (i < criteria.getTypes().size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(") ");
            for (typeInsertion type : criteria.getTypes()) {
                parameters.add(type.name());
            }
        }

        sql.append("ORDER BY ");
        if ("price_asc".equals(criteria.getSortBy())) {
            sql.append("i.price ASC ");
        } else if ("price_desc".equals(criteria.getSortBy())) {
            sql.append("i.price DESC ");
        } else {
            sql.append("i.publishDate DESC ");
        }

        return executeFilterQuery(sql.toString(), parameters);
    }
    @Override
    public List<Insertion> findAvailableInsertionsByUserId(int userId) throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.userid = ? AND i.status IN ('AVAILABLE', 'PENDING') ORDER BY i.publishDate DESC";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Insertion insertion = createInsertionFromResultSet(resultSet);
                    insertions.add(insertion);
                }
            }
        }
        return insertions;
    }

    @Override
    public BigDecimal getMaxPrice() throws SQLException {
        String sql = "SELECT MAX(price) as max_price FROM insertion WHERE status = 'AVAILABLE' AND price IS NOT NULL";
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
        String sql = "SELECT MIN(price) as min_price FROM insertion WHERE status = 'AVAILABLE' AND price IS NOT NULL AND price > 0";
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
    public void updateInsertionStatus(int insertionID, InsertionStatus status){
        String sql = "UPDATE insertion SET status = ? WHERE insertionid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, insertionID);
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
    public List<Insertion> findInsertionsExcludingCurrentUser() throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.status = 'AVAILABLE' AND i.userid != ? " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(insertions, stmt);
        }
        return insertions;
    }

    @Override
    public List<Insertion> findCurrentUserAvailableInsertions() throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.status = 'AVAILABLE' AND i.userid = ? " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(insertions, stmt);
        }
        return insertions;
    }

    @Override
    public List<Insertion> findCurrentUserInsertions() throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.userid = ? " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(insertions, stmt);
        }
        return insertions;
    }

    @Override
    public List<Insertion> findByCategory(int categoryId) throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.category_id = ? AND i.status = 'AVAILABLE' AND i.userid != ? " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);
            stmt.setInt(2, UserSession.getInstance().getCurrentUser().getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Insertion insertion = createInsertionFromResultSet(rs);
                        insertions.add(insertion);
                    } catch (Exception e) {
                        System.err.println("Errore nel processare la lista: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero degli annunci per categoria " + categoryId + ": " + e.getMessage());
            throw e;
        }
        return insertions;
    }

    @Override
    public List<Insertion> findByStatus(String status) throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.status = ? AND i.userid != ? " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(insertions, stmt);
        }
        return insertions;
    }

    @Override
    public List<Insertion> findByFaculty(int facultyId) throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.faculty_id = ? AND i.status = 'AVAILABLE' AND i.userid != ? " +
                "ORDER BY i.publishDate DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, facultyId);
            stmt.setInt(2, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(insertions, stmt);
        }
        return insertions;
    }

    @Override
    public List<Insertion> findByPriceRange(double minPrice, double maxPrice) throws SQLException {
        List<Insertion> insertions = new ArrayList<>();
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE price BETWEEN ? AND ? AND i.userid = ? ORDER BY publishDate DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, minPrice);
            stmt.setDouble(2, maxPrice);
            stmt.setInt(3, UserSession.getInstance().getCurrentUser().getId());
            executeQueryAndCreateTheList(insertions, stmt);
        }
        return insertions;
    }

    @Override
    public Insertion findInsertionById(int insertionID) {
        String sql = "SELECT i.*, c.name as category_name FROM insertion i " +
                "LEFT JOIN category c ON i.category_id = c.category_id " +
                "WHERE i.insertionid = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, insertionID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createInsertionFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero dell'annuncio con ID " + insertionID + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void populateInsertionStatement(PreparedStatement stmt, Insertion insertion) throws SQLException {
        stmt.setString(1, insertion.getTitle());
        stmt.setString(2, insertion.getImageUrl());
        stmt.setString(3, insertion.getDescription());
        stmt.setString(4, insertion.getDeliveryMethod());
        stmt.setString(5, insertion.getType().name());

        switch (insertion) {
            case SaleInsertion saleListing -> stmt.setBigDecimal(6, saleListing.getPrice());
            case ExchangeInsertion exchangeListing -> stmt.setBigDecimal(6, exchangeListing.getPrice());
            case GiftInsertion giftListing -> stmt.setBigDecimal(6, BigDecimal.ZERO);
            default -> {
                stmt.setBigDecimal(6, BigDecimal.ZERO);
                throw new SQLException("Tipo di inserzione sconosciuto");
            }
        }

        stmt.setString(7, insertion.getStatus().name());
        stmt.setDate(8, new java.sql.Date(insertion.getPublishDate().toEpochDay() * 24 * 60 * 60 * 1000));
        stmt.setInt(9, insertion.getUserId());

        CategoryDaoImpl category = new CategoryDaoImpl();
        int categoryId = category.getCategoryIdByName(insertion.getCategory());
        stmt.setInt(10, categoryId);
    }

    public Insertion createInsertionFromResultSet(ResultSet rs) throws SQLException {
        int listingId = rs.getInt("insertionid");
        String title = rs.getString("title");
        String imageUrl = rs.getString("imageUrl");
        String description = rs.getString("description");
        typeInsertion type = parseType(rs.getString("type"));
        InsertionStatus status = parseStatus(rs.getString("status"));
        java.time.LocalDate publishDate = rs.getDate("publishDate").toLocalDate();
        int userId = rs.getInt("userId");
        String category = rs.getString("category_name");
        BigDecimal price = rs.getBigDecimal("price");
        String deliveryMethod = rs.getString("delivery_method");

        Insertion insertion = InsertionFactory.createListing(title, imageUrl, description, type, price, status, publishDate, userId, category, deliveryMethod);
        insertion.setInsertionID(listingId);
        return insertion;
    }

    private void executeQueryAndCreateTheList(List<Insertion> insertions, PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                try {
                    Insertion insertion = createInsertionFromResultSet(rs);
                    insertions.add(insertion);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("Errore nel processare la lista " + e.getMessage());
                }
            }
        }
    }

    private typeInsertion parseType(String typeStr) {
        try {
            return typeInsertion.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Tipo sconosciuto " + typeStr + " - default a SALE");
            return typeInsertion.SALE;
        }
    }

    private InsertionStatus parseStatus(String statusStr) {
        try {
            return InsertionStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            System.err.println("Stato sconosciuto " + statusStr + " - default a AVAILABLE");
            return InsertionStatus.AVAILABLE;
        }
    }

    private List<Insertion> executeFilterQuery(String sql, List<Object> parameters) throws SQLException {
        List<Insertion> insertions = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Insertion insertion = createInsertionFromResultSet(rs);
                    insertions.add(insertion);
                }
            }
        }
        return insertions;
    }

    private void logDatabaseError(String operation, Exception e){
        e.printStackTrace();
        System.err.println("Errore durante l'operazione '" + operation + "': " + e.getMessage());
    }
}