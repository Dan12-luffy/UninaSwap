package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SaleInsertion extends Insertion {
    private BigDecimal insertionPrice;

    public SaleInsertion() {
        super();
    }
    public SaleInsertion(Integer insertionID, String title, String imageUrl, String description,
                         typeInsertion type, BigDecimal insertionPrice, InsertionStatus status,
                         LocalDate publishDate, Integer userId, String category) {
        super(insertionID, title, imageUrl, description, type ,status, publishDate, userId, category);
        this.insertionPrice = insertionPrice;
    }
    // Costruttore senza id
    public SaleInsertion(String title, String imageUrl, String description,
                         typeInsertion type, BigDecimal price, InsertionStatus status,
                         LocalDate publishDate, Integer userId, String category) {
        super(title, imageUrl, description, type, status, publishDate, userId, category);
        this.insertionPrice = price;
    }

    @Override
    public typeInsertion getInsertionType() {
        return typeInsertion.SALE;
    }
    @Override
    public BigDecimal getPrice() {
        return insertionPrice;
    }

    public void setPrice(BigDecimal price) {
        this.insertionPrice = price;
    }
}
