package com.uninaswap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public abstract class Insertion {
    private int insertionID;
    private String title;
    private String imageUrl;
    private String description;
    private typeInsertion type; //ENUM
    private InsertionStatus status; //ENUM
    private LocalDate publishDate;

    private int userId;
    private String category;
    private String deliveryMethod;
    private Integer categoryId;

    protected Insertion() {}

    // Costruttore completo
    protected Insertion(Integer insertionID, String title, String imageUrl, String description,
                        typeInsertion type, InsertionStatus status,
                        LocalDate publishDate, Integer userId, String category) {
        this.insertionID = insertionID;
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
    protected Insertion(String title, String imageUrl, String description,
                        typeInsertion type, InsertionStatus status,
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
    public Integer getInsertionID() {
        return insertionID;
    }
    public Integer getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
    public void setInsertionID(Integer insertionID) {
        this.insertionID = insertionID;
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
    public typeInsertion getType() {
        return type;
    }
    public void setType(typeInsertion type) {
        this.type = type;
    }
    public InsertionStatus getStatus() {
        return status;
    }
    public void setStatus(InsertionStatus status) {
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
    public String getDeliveryMethod() {
        return deliveryMethod;
    }
    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
    public abstract typeInsertion getInsertionType();
    public abstract BigDecimal getPrice();

    @Override
    public String toString() {
        return "Insertion{" +
                "insertionID=" + insertionID +
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



