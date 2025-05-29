package com.uninaswap.services;

import com.uninaswap.dao.ListingDao;
import com.uninaswap.dao.ListingDaoImpl;
import com.uninaswap.databaseUtils.FilterCriteria;
import com.uninaswap.model.Listing;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class FilterService {
    private final static FilterService instance = new FilterService();
    private final ListingDao listingDao;

    private FilterService() {
        this.listingDao = new ListingDaoImpl();
    }

    public static FilterService getInstance() {
        return instance;
    }

    public List<Listing> searchListings(FilterCriteria criteria) throws SQLException {
        // Imposta l'utente corrente da escludere se non specificato
        if (criteria.getExcludeUserId() == null && UserSession.getInstance().getCurrentUser() != null) {
            criteria.setExcludeUserId(UserSession.getInstance().getCurrentUser().getId());
        }
        return listingDao.findWithFilters(criteria);
    }

    public BigDecimal getMaxAvailablePrice() throws SQLException {
        return listingDao.getMaxPrice();
    }

    public BigDecimal getMinAvailablePrice() throws SQLException {
        return listingDao.getMinPrice();
    }

    // Metodi di convenienza
    public List<Listing> searchByText(String text) throws SQLException {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setSearchText(text);
        return searchListings(criteria);
    }

    public List<Listing> searchByCategories(List<Integer> categoryIds) throws SQLException {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setCategoryIds(categoryIds);
        return searchListings(criteria);
    }

    public List<Listing> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setMinPrice(minPrice);
        criteria.setMaxPrice(maxPrice);
        return searchListings(criteria);
    }
}