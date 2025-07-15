package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleInsertion extends Insertion {
    private BigDecimal listingPrice;

    public SaleInsertion() {
        super();
    }
    public SaleInsertion(Integer listingId, String title, String imageUrl, String description,
                         typeInsertion type, BigDecimal listingPrice, InsertionStatus status,
                         LocalDate publishDate, Integer userId, String category) {
        super(listingId, title, imageUrl, description, type ,status, publishDate, userId, category);
        this.listingPrice = listingPrice;
    }
    // Costruttore senza id
    public SaleInsertion(String title, String imageUrl, String description,
                         typeInsertion type, BigDecimal price, InsertionStatus status,
                         LocalDate publishDate, Integer userId, String category) {
        super(title, imageUrl, description, type, status, publishDate, userId, category);
        this.listingPrice = price;
    }

    @Override
    public typeInsertion getListingType() {
        return typeInsertion.SALE;
    }
    @Override
    public BigDecimal getPrice() {
        return listingPrice;
    }

    public void setPrice(BigDecimal price) {
        this.listingPrice = price;
    }
}
