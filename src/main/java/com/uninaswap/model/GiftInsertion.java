package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GiftInsertion extends Insertion {
    public GiftInsertion() {
        super();
    }

    public GiftInsertion(String title, String imageUrl, String description,
                         typeInsertion type, InsertionStatus status,
                         LocalDate publishDate, Integer userId, String category, String deliveryMethod) {
        super(title, imageUrl, description, type, status, publishDate, userId, category, deliveryMethod);
    }

    @Override
    public typeInsertion getInsertionType() {
        return typeInsertion.GIFT;
    }
    @Override
    public BigDecimal getPrice() {
        return BigDecimal.ZERO;
    }
}