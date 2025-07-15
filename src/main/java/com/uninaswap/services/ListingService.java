package com.uninaswap.services;

import com.uninaswap.dao.InsertionDao;
import com.uninaswap.dao.InsertionDaoImpl;
import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Insertion;
import com.uninaswap.model.InsertionStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ListingService {
    private static final ListingService instance = new ListingService();
    private final InsertionDao insertionDao;
    private final UserDao userDao;

    private ListingService() {
        this.insertionDao = new InsertionDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    public static ListingService getInstance() {
        return instance;
    }

    public List<Insertion> getAllListings() throws SQLException {
        return insertionDao.findAll();
    }
    public void createListing(Insertion insertion) throws SQLException {
        insertionDao.insert(insertion);
    }
    public void updateListing(Insertion insertion) throws SQLException {
        insertionDao.update(insertion);
    }
    public void deleteListing(int listingId) throws SQLException {
        insertionDao.delete(listingId);
    }
    /*public Listing findById(int listingId) throws SQLException {
        return listingDao.findById(listingId);
    }*/

    public String getSellerFullName(int userId) {
        return userDao.findFullNameFromID(userId);
    }
    public List<Insertion> getListingsByFilters(FilterCriteria criteria) throws SQLException {
        return insertionDao.findByFilters(criteria);
    }
    public BigDecimal getInsertionMaxPrice() throws SQLException {
        return insertionDao.getMaxPrice();
    }
    public BigDecimal getInsertionMinPrice() throws SQLException {
        return insertionDao.getMinPrice();
    }
    public List<Insertion> getInsertionsExcludingCurrentUser() throws SQLException {
        return insertionDao.findInsertionsExcludingCurrentUser();
    }
    public List<Insertion> getCurrentUserAvailableInsertions() throws SQLException {
        return insertionDao.findCurrentUserAvailableInsertions();
    }
    public List<Insertion> getCurrentUserInsertions() throws SQLException {
        return insertionDao.findCurrentUserInsertions();
    }
    public List<Insertion> getListingsByCategory(int categoryId) throws SQLException {
        return insertionDao.findByCategory(categoryId);
    }
    public List<Insertion> getListingsByStatus(String status) throws SQLException {
        return insertionDao.findByStatus(status);
    }
    public List<Insertion> getInsertionsByFaculty(int facultyId) throws SQLException {
        return insertionDao.findByFaculty(facultyId);
    }
    public Insertion getListingByID(int listingId) throws SQLException {
        return insertionDao.findListingById(listingId);
    }

    public void updateListingStatus(Integer listingId, InsertionStatus insertionStatus) {
        try {
            insertionDao.updateListingStatus(listingId, insertionStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating listing status", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while updating listing status", e);
        }
    }
}
