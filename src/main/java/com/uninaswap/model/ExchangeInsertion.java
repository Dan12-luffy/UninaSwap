package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeInsertion extends Insertion {
    private BigDecimal listingPrice;
    public ExchangeInsertion() {
        super();
    }

    public ExchangeInsertion(String title, String imageUrl, String description,
                             typeInsertion type, BigDecimal price, InsertionStatus status,
                             LocalDate publishDate, Integer userId, String category) {
        super(title, imageUrl, description, type, status, publishDate, userId, category);
        this.listingPrice = price;
    }

    @Override
    public typeInsertion getListingType() {
        return typeInsertion.EXCHANGE;
    }
    @Override
    public BigDecimal getPrice() {
        return listingPrice;
    }
    public void setPrice(BigDecimal price) {
        this.listingPrice = price;
    }
}
