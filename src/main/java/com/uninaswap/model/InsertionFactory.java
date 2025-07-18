package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InsertionFactory {

    public static Insertion createInsertion(String title, String imageUrl, String description,
                                            typeInsertion type, BigDecimal insertionPrice, InsertionStatus status,
                                            LocalDate publishDate, Integer userId, String category, String deliveryMethod) {

        Insertion insertion = switch (type) {
            case EXCHANGE -> new ExchangeInsertion(title, imageUrl, description, type, insertionPrice, status, publishDate, userId, category);
            case GIFT -> new GiftInsertion(title, imageUrl, description, type, status, publishDate, userId, category);
            case SALE -> new SaleInsertion(title, imageUrl, description, type, insertionPrice, status, publishDate, userId, category);
            default -> throw new IllegalArgumentException("Tipo invalido: " + type);
        };

        insertion.setDeliveryMethod(deliveryMethod);
        return insertion;
    }

}
