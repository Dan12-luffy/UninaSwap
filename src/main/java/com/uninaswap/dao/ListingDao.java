package com.uninaswap.dao;

import com.uninaswap.model.Listing;
import java.sql.SQLException;
import java.util.List;

public interface ListingDao {
    /*
    * insert
    * delete
    * update
    * find all
    * find by category
    * find by status
    * find by type
    * TODO : find by userId
    * */

    void insert(Listing listing) throws SQLException;
    void delete(int listingId) throws SQLException;
    void update(Listing listing) throws SQLException;
    List<Listing> findAll() throws SQLException;
    List<Listing> findByCategory(int categoryId) throws SQLException;
    List<Listing> findByType(String type) throws SQLException;
    List<Listing> findByPriceRange(double minPrice, double maxPrice) throws SQLException;

}
