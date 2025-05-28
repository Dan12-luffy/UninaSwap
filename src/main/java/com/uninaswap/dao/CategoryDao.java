package com.uninaswap.dao;

import com.uninaswap.model.Category;

import java.util.List;

public interface CategoryDao {
    //CRUD Operations
    void insert(Category category) throws Exception;
    void delete(int categoryId) throws Exception;
    void update(int categoryId, String newCategoryName) throws Exception;
    String findById(int categoryId) throws Exception;
    String findByName(String categoryName) throws Exception;
    List<Category> findAll() throws Exception;
    int getCategoryIdByName(String categoryName) throws Exception;
    String getCategoryNameById(int categoryId) throws Exception;
}
