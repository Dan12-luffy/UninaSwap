package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InsertionFactory {
    public static Insertion createListing(String title, String imageUrl, String description,
                                          typeInsertion type, BigDecimal listingPrice, InsertionStatus status,
                                          LocalDate publishDate, Integer userId, String category) {
        return switch (type) {
            case EXCHANGE -> new ExchangeInsertion(title, imageUrl, description, type, listingPrice, status, publishDate, userId, category);

            case GIFT -> new GiftInsertion(title, imageUrl, description, type, status, publishDate, userId, category);

            case SALE -> new SaleInsertion(title, imageUrl, description, type, listingPrice, status, publishDate, userId, category);

            default -> throw new IllegalArgumentException("Tipo invalido: " + type);
        };
    }
}
