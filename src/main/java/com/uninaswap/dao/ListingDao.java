package com.uninaswap.dao;

import com.uninaswap.model.Listing;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public interface ListingDao {
    // CRUD operations

    void insert(Listing listing) throws SQLException;
    void delete(int listingId) throws SQLException;
    void update(Listing listing) throws SQLException;
    List<Listing> findAll() throws SQLException;
    List<Listing> findAllOtherInsertions() throws SQLException;
    List<Listing> findMyInsertions() throws SQLException;
    List<Listing> findByCategory(int categoryId) throws SQLException;
    List<Listing> findByPriceRange(double minPrice, double maxPrice) throws SQLException;

    List<Listing> findByStatus(String status) throws SQLException;
    List<Listing> findByText(String text) throws SQLException;
    List<Listing> findByFaculty(int facultyId) throws SQLException;
}
