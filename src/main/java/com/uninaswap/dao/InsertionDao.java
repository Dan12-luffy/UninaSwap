package com.uninaswap.dao;

import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Insertion;
import com.uninaswap.model.InsertionStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface InsertionDao {
    // CRUD operations

    void insert(Insertion insertion) throws SQLException;
    void delete(int listingId) throws SQLException;
    void update(Insertion insertion) throws SQLException;
    List<Insertion> findAll() throws SQLException;

    void updateListingStatus(int listingId, InsertionStatus status) throws SQLException;

    List<Insertion> findInsertionsExcludingCurrentUser() throws SQLException;
    List<Insertion> findCurrentUserAvailableInsertions() throws SQLException;
    Insertion findListingById(int listingId) throws SQLException;

    List<Insertion> findCurrentUserInsertions() throws SQLException;

    List<Insertion> findByCategory(int categoryId) throws SQLException;
    List<Insertion> findByPriceRange(double minPrice, double maxPrice) throws SQLException;
    List<Insertion> findByFilters(FilterCriteria criteria) throws SQLException;


    BigDecimal getMaxPrice() throws SQLException;
    BigDecimal getMinPrice() throws SQLException;
    List<Insertion> findByStatus(String status) throws SQLException;
    List<Insertion> findByFaculty(int facultyId) throws SQLException;

}
