package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExchangeInsertion extends Insertion {
    private BigDecimal insertionPrice;
    public ExchangeInsertion() {
        super();
    }

    public ExchangeInsertion(String title, String imageUrl, String description,
                             typeInsertion type, BigDecimal price, InsertionStatus status,
                             LocalDate publishDate, Integer userId, String category, String deliveryMethod) {
        super(title, imageUrl, description, type, status, publishDate, userId, category, deliveryMethod);
        this.insertionPrice = price;
    }

    @Override
    public typeInsertion getInsertionType() {
        return typeInsertion.EXCHANGE;
    }
    @Override
    public BigDecimal getPrice() {
        return insertionPrice;
    }
    public void setPrice(BigDecimal price) {
        this.insertionPrice = price;
    }
}
