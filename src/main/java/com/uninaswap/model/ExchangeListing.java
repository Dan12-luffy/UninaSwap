package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeListing extends Listing {
    private BigDecimal listingPrice;
    public ExchangeListing() {
        super();
    }

    public ExchangeListing(String title, String imageUrl, String description,
                           typeListing type, BigDecimal price, ListingStatus status,
                           LocalDate publishDate, Integer userId, String category) {
        super(title, imageUrl, description, type, status, publishDate, userId, category);
        this.listingPrice = price;
    }

    @Override
    public typeListing getListingType() {
        return typeListing.EXCHANGE;
    }
    @Override
    public BigDecimal getPrice() {
        return listingPrice;
    }
    public void setPrice(BigDecimal price) {
        this.listingPrice = price;
    }
}
