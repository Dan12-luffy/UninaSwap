package com.uninaswap.dao;

import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Listing;
import com.uninaswap.model.ListingStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ListingDao {
    // CRUD operations

    void insert(Listing listing) throws SQLException;
    void delete(int listingId) throws SQLException;
    void update(Listing listing) throws SQLException;
    List<Listing> findAll() throws SQLException;

    void updateListingStatus(int listingId, ListingStatus status) throws SQLException;

    List<Listing> findInsertionsExcludingCurrentUser() throws SQLException;
    List<Listing> findCurrentUserAvailableInsertions() throws SQLException;
    Listing findListingById(int listingId) throws SQLException;

    List<Listing> findCurrentUserInsertions() throws SQLException;

    List<Listing> findByCategory(int categoryId) throws SQLException;
    List<Listing> findByPriceRange(double minPrice, double maxPrice) throws SQLException;
    List<Listing> findByFilters(FilterCriteria criteria) throws SQLException;


    BigDecimal getMaxPrice() throws SQLException;
    BigDecimal getMinPrice() throws SQLException;
    List<Listing> findByStatus(String status) throws SQLException;
    List<Listing> findByFaculty(int facultyId) throws SQLException;

}
