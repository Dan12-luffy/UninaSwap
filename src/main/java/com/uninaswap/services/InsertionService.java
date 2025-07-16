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

public class InsertionService {
    private static final InsertionService instance = new InsertionService();
    private final InsertionDao insertionDao;
    private final UserDao userDao;

    private InsertionService() {
        this.insertionDao = new InsertionDaoImpl();
        this.userDao = new UserDaoImpl();
    }

    public static InsertionService getInstance() {
        return instance;
    }

    public List<Insertion> getAllInsertion() throws SQLException {
        return insertionDao.findAll();
    }
    public void createInsertion(Insertion insertion) throws SQLException {
        insertionDao.insert(insertion);
    }
    public void updateInsertion(Insertion insertion) throws SQLException {
        insertionDao.update(insertion);
    }
    public void deleteInsertion(int insertionID) throws SQLException {
        insertionDao.delete(insertionID);
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
    public List<Insertion> getInsertionsByCategory(int categoryId) throws SQLException {
        return insertionDao.findByCategory(categoryId);
    }
    public List<Insertion> getInsertionsByStatus(String status) throws SQLException {
        return insertionDao.findByStatus(status);
    }
    public List<Insertion> getInsertionsByFaculty(int facultyId) throws SQLException {
        return insertionDao.findByFaculty(facultyId);
    }
    public Insertion getInsertionByID(int insertionID) throws SQLException {
        return insertionDao.findInsertionById(insertionID);
    }

    public void updateInsertionStatus(Integer insertionID, InsertionStatus insertionStatus) {
        try {
            insertionDao.updateInsertionStatus(insertionID, insertionStatus);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating listing status", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while updating listing status", e);
        }
    }
}
