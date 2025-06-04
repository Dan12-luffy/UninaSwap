package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleListing extends Listing{
    private BigDecimal listingPrice;

    public SaleListing() {
        super();
    }
    public SaleListing(Integer listingId, String title, String imageUrl, String description,
                       typeListing type, BigDecimal listingPrice, ListingStatus status,
                       LocalDate publishDate, Integer userId, String category) {
        super(listingId, title, imageUrl, description, type ,status, publishDate, userId, category);
        this.listingPrice = listingPrice;
    }
    // Costruttore senza id
    public SaleListing(String title, String imageUrl, String description,
                       typeListing type, BigDecimal price, ListingStatus status,
                       LocalDate publishDate, Integer userId, String category) {
        super(title, imageUrl, description, type, status, publishDate, userId, category);
        this.listingPrice = price;
    }

    @Override
    public typeListing getListingType() {
        return typeListing.SALE;
    }
    @Override
    public BigDecimal getPrice() {
        return listingPrice;
    }

    public void setPrice(BigDecimal price) {
        this.listingPrice = price;
    }
}
