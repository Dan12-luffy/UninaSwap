package com.uninaswap.services;

import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.dao.UserDao;
import com.uninaswap.dao.UserDaoImpl;
import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Listing;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ListingService {
    private static final ListingService instance = new ListingService();
    private final ListingDao listingDao;
    private final UserDao userDao;

    private ListingService() {
        this.listingDao = new ListingDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    public static ListingService getInstance() {
        return instance;
    }

    public List<Listing> getAllListings() throws SQLException {
        return listingDao.findAll();
    }
    public void createListing(Listing listing) throws SQLException {
        listingDao.insert(listing);
    }
    public void updateListing(Listing listing) throws SQLException {
        listingDao.update(listing);
    }
    public void deleteListing(int listingId) throws SQLException {
        listingDao.delete(listingId);
    }
    /*public Listing findById(int listingId) throws SQLException {
        return listingDao.findById(listingId);
    }*/

    public String getSellerFullName(int userId) {
        return userDao.findFullNameFromID(userId);
    }
    public List<Listing> getListingsByFilters(FilterCriteria criteria) throws SQLException {
        return listingDao.findByFilters(criteria);
    }
    public BigDecimal getInsertionMaxPrice() throws SQLException {
        return listingDao.getMaxPrice();
    }
    public BigDecimal getInsertionMinPrice() throws SQLException {
        return listingDao.getMinPrice();
    }
    public List<Listing> getInsertionsExcludingCurrentUser() throws SQLException {
        return listingDao.findInsertionsExcludingCurrentUser();
    }
    public List<Listing> getCurrentUserAvailableInsertions() throws SQLException {
        return listingDao.findCurrentUserAvailableInsertions();
    }
    public List<Listing> getCurrentUserInsertions() throws SQLException {
        return listingDao.findCurrentUserInsertions();
    }
    public List<Listing> getListingsByCategory(int categoryId) throws SQLException {
        return listingDao.findByCategory(categoryId);
    }
    public List<Listing> getListingsByStatus(String status) throws SQLException {
        return listingDao.findByStatus(status);
    }
    public List<Listing> getInsertionsByFaculty(int facultyId) throws SQLException {
        return listingDao.findByFaculty(facultyId);
    }
    public Listing getListingByID(int listingId) throws SQLException {
        return listingDao.findListingById(listingId);
    }
}
