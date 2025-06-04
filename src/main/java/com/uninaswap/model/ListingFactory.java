package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ListingFactory {
    public static Listing createListing(String title, String imageUrl, String description,
                                        typeListing type, BigDecimal listingPrice, ListingStatus status,
                                        LocalDate publishDate, Integer userId, String category) {
        return switch (type) {
            case EXCHANGE -> new ExchangeListing(title, imageUrl, description, type, listingPrice, status, publishDate, userId, category);

            case GIFT -> new GiftListing(title, imageUrl, description, type, status, publishDate, userId, category);

            case SALE -> new SaleListing(title, imageUrl, description, type, listingPrice, status, publishDate, userId, category);

            default -> throw new IllegalArgumentException("Tipo invalido: " + type);
        };
    }
}
