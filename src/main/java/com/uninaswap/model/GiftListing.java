package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GiftListing extends Listing {
    public GiftListing() {
        super();
    }

    public GiftListing(String title, String imageUrl, String description,
                       typeListing type, ListingStatus status,
                       LocalDate publishDate, Integer userId, String category) {
        super(title, imageUrl, description, type, status, publishDate, userId, category);
    }

    @Override
    public typeListing getListingType() {
        return typeListing.GIFT;
    }
    @Override
    public BigDecimal getPrice() {
        return BigDecimal.ZERO;
    }
}