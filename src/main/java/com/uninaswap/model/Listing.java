package com.uninaswap.model;

import java.math.BigDecimal;
import java.util.Date;

public class Listing {
    private int listingId;
    private String title;
    private String imageUrl;
    private String description;
    private typeListing type; //ENUM
    private BigDecimal price;
    private ListingStatus status; //ENUM
    private Date publishDate;
    private int userId;
    private int categoryId;

    public Listing() {}

    // Costruttore completo
    public Listing(Integer listingId, String title, String imageUrl, String description,
                   typeListing type, BigDecimal price, ListingStatus status,
                   Date publishDate, Integer userId, Integer categoryId) {
        this.listingId = listingId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.type = type;
        this.price = price;
        this.status = status;
        this.publishDate = publishDate;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    // Getter e Setter
    public Integer getListingId() {
        return listingId;
    }

    public void setListingId(Integer listingId) {
        this.listingId = listingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public typeListing getType() {
        return type;
    }

    public void setType(typeListing type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Listing{" +
                "listingId=" + listingId +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", status=" + status +
                ", publishDate=" + publishDate +
                ", userId=" + userId +
                ", categoryId=" + categoryId +
                '}';
    }
}



