package com.uninaswap.services;

import com.uninaswap.dao.InsertionDao;
import com.uninaswap.dao.InsertionDaoImpl;
import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Insertion;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class FilterService {
    private final static FilterService instance = new FilterService();
    private final InsertionDao insertionDao;

    private FilterService() {
        this.insertionDao = new InsertionDaoImpl();
    }

    public static FilterService getInstance() {
        return instance;
    }

    public List<Insertion> searchInsertions(FilterCriteria criteria) throws SQLException {
        // Imposta l'utente corrente da escludere se non specificato
        if (criteria.getExcludeUserId() == null && UserSession.getInstance().getCurrentUser() != null) {
            criteria.setExcludeUserId(UserSession.getInstance().getCurrentUser().getId());
        }
        return insertionDao.findByFilters(criteria);
    }

    public BigDecimal getMaxAvailablePrice() throws SQLException {
        return insertionDao.getMaxPrice();
    }

    public BigDecimal getMinAvailablePrice() throws SQLException {
        return insertionDao.getMinPrice();
    }

    // Metodi di convenienza
    public List<Insertion> searchByText(String text) throws SQLException {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setSearchText(text);
        return searchInsertions(criteria);
    }

    public List<Insertion> searchByCategories(List<Integer> categoryIds) throws SQLException {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setCategoryIds(categoryIds);
        return searchInsertions(criteria);
    }

    public List<Insertion> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setMinPrice(minPrice);
        criteria.setMaxPrice(maxPrice);
        return searchInsertions(criteria);
    }
}