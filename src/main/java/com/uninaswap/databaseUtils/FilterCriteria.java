package com.uninaswap.databaseUtils;

import com.uninaswap.model.typeListing;

import java.math.BigDecimal;
import java.util.List;

public class FilterCriteria {
    private String searchText;
    private List<Integer> categoryIds;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy; // "date_desc", "price_asc", "price_desc"
    private String status;
    private typeListing type;
    private Integer facultyId;
    private Integer excludeUserId; // Per escludere gli annunci dell'utente corrente

    // Constructors
    public FilterCriteria() {}

    public FilterCriteria(String searchText, List<Integer> categoryIds, BigDecimal minPrice, BigDecimal maxPrice) {
        this.searchText = searchText;
        this.categoryIds = categoryIds;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    // Getters and Setters
    public String getSearchText() { return searchText; }
    public void setSearchText(String searchText) { this.searchText = searchText; }

    public List<Integer> getCategoryIds() { return categoryIds; }
    public void setCategoryIds(List<Integer> categoryIds) { this.categoryIds = categoryIds; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public typeListing getType() { return type; }
    public void setType(typeListing type) { this.type = type; }

    public Integer getFacultyId() { return facultyId; }
    public void setFacultyId(Integer facultyId) { this.facultyId = facultyId; }

    public Integer getExcludeUserId() { return excludeUserId; }
    public void setExcludeUserId(Integer excludeUserId) { this.excludeUserId = excludeUserId; }

    public boolean hasTextSearch() {
        return searchText != null && !searchText.trim().isEmpty();
    }

    public boolean hasCategoryFilter() {
        return categoryIds != null && !categoryIds.isEmpty();
    }

    public boolean hasPriceFilter() {
        return minPrice != null || maxPrice != null;
    }
}
