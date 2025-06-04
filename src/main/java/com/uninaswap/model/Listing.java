package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public abstract class Listing {
    private int listingId;
    private String title;
    private String imageUrl;
    private String description;
    private typeListing type; //ENUM
    private ListingStatus status; //ENUM
    private LocalDate publishDate;

    private int userId;
    private String category;
    private Integer categoryId;

    protected Listing() {}

    // Costruttore completo
    protected Listing(Integer listingId, String title, String imageUrl, String description,
                   typeListing type, ListingStatus status,
                   LocalDate publishDate, Integer userId, String category) {
        this.listingId = listingId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.type = type;
        this.status = status;
        this.publishDate = publishDate;
        this.userId = userId;
        this.category = category;
    }
    //Costruttore senza id
    protected Listing(String title, String imageUrl, String description,
                   typeListing type, ListingStatus status,
                   LocalDate publishDate, Integer userId, String category) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.type = type;
        this.status = status;
        this.publishDate = publishDate;
        this.userId = userId;
        this.category = category;
    }

    // Getter e Setter
    public Integer getListingId() {
        return listingId;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
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


    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public abstract typeListing getListingType();

    public abstract BigDecimal getPrice();

    @Override
    public String toString() {
        return "Listing{" +
                "listingId=" + listingId +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", publishDate=" + publishDate +
                ", userId=" + userId +
                ", categoryId=" + category +
                '}';
    }
}



